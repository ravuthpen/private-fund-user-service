package com.fund.server.accountservice.service.impl;

import com.fund.server.accountservice.config.MinioProperties;
import com.fund.server.accountservice.dto.CustomerProfileDetailResponse;
import com.fund.server.accountservice.dto.CustomerProfileResponse;
import com.fund.server.accountservice.exception.ProfileNotFoundException;
import com.fund.server.accountservice.mapper.CustomerProfileDetailMapper;
import com.fund.server.accountservice.repository.CustomerProfileRepository;
import com.fund.server.accountservice.service.UserMediaService;
import com.fund.server.accountservice.service.storage.ObjectStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMediaServiceImpl implements UserMediaService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final int DEFAULT_MAX_FILES = 10;
    private static final long DEFAULT_MAX_FILE_SIZE = 5_000_000L;
    private static final int UPLOAD_CONCURRENCY = 3;
    private static final int URL_CONCURRENCY = 5;
    private static final Duration PRESIGNED_URL_DURATION = Duration.ofHours(6);

    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerProfileDetailMapper customerProfileDetailMapper;
    private final ObjectStorage objectStorage;
    private final MinioProperties props;

    @Override
    public Mono<CustomerProfileDetailResponse> uploadUserPhoto(
            UUID customerId,
            UUID userId,
            Flux<FilePart> files) {

        return customerProfileRepository.findById(customerId)

                // ---------------------------------------------------------
                // 1. Check profile exists
                // ---------------------------------------------------------
                .switchIfEmpty(
                        Mono.error(
                                new ProfileNotFoundException(customerId)
                        )
                )

                // ---------------------------------------------------------
                // 2. Check profile belongs to current user
                // ---------------------------------------------------------
                .flatMap(profile -> {

                    if (!Objects.equals(
                            profile.getUserAccountId(),
                            userId
                    )) {
                        return Mono.error(
                                new IllegalStateException(
                                        "You do not own this profile image"
                                )
                        );
                    }

                    int existingCount =
                            profile.getPhotoObjectKey() == null
                                    ? 0
                                    : profile.getPhotoObjectKey().size();

                    int maxFiles =
                            props.maxFilesPerUser() == null
                                    ? DEFAULT_MAX_FILES
                                    : props.maxFilesPerUser();

                    return files
                            .switchIfEmpty(
                                    Mono.error(
                                            new IllegalStateException(
                                                    "File is required"
                                            )
                                    )
                            )

                            // -------------------------------------------------
                            // 3. Upload files
                            // -------------------------------------------------
                            .flatMap(
                                    file -> validateAndUploadOne(
                                            customerId,
                                            file
                                    ),
                                    UPLOAD_CONCURRENCY
                            )

                            // Mono<List<String>>
                            .collectList()

                            // -------------------------------------------------
                            // 4. Check max number of photos
                            // -------------------------------------------------
                            .flatMap(uploadedKeys -> {

                                if (existingCount + uploadedKeys.size()
                                        > maxFiles) {

                                    log.warn(
                                            "Photo limit exceeded. customerId={}, existing={}, uploaded={}, max={}",
                                            customerId,
                                            existingCount,
                                            uploadedKeys.size(),
                                            maxFiles
                                    );

                                    return removeObjects(uploadedKeys)
                                            .then(
                                                    Mono.error(
                                                            new IllegalStateException(
                                                                    "Too many photos. Max="
                                                                            + maxFiles
                                                            )
                                                    )
                                            );
                                }

                                // -------------------------------------------------
                                // 5. Merge old + new object keys
                                // -------------------------------------------------
                                List<String> mergedKeys =
                                        new ArrayList<>(
                                                profile.getPhotoObjectKey() == null
                                                        ? List.of()
                                                        : profile.getPhotoObjectKey()
                                        );

                                mergedKeys.addAll(uploadedKeys);

                                profile.setPhotoObjectKey(mergedKeys);

                                // -------------------------------------------------
                                // 6. Save profile
                                // -------------------------------------------------
                                return customerProfileRepository
                                        .save(profile)

                                        // If DB save fails, remove uploaded
                                        // objects from MinIO.
                                        .onErrorResume(ex ->
                                                removeObjects(uploadedKeys)
                                                        .then(
                                                                Mono.error(ex)
                                                        )
                                        );
                            });
                })

                // -------------------------------------------------------------
                // IMPORTANT:
                //
                // Previous operation returns:
                //
                // Mono<CustomerProfile>
                //
                // So we CANNOT do:
                //
                // .flatMap(this::toRoomResponseWithUrls)
                //
                // because that method expects:
                //
                // CustomerProfileDetailResponse
                //
                // -------------------------------------------------------------
                .flatMap(savedProfile -> {

                    /*
                     * Reload the complete profile detail.
                     *
                     * This gives us:
                     *
                     * account
                     * profile
                     * address
                     *
                     * instead of only CustomerProfile.
                     */

                    return customerProfileRepository
                            .findCustomerById(userId)
                            .collectList()

                            .filter(details -> !details.isEmpty())

                            .switchIfEmpty(
                                    Mono.error(
                                            new ProfileNotFoundException(
                                                    savedProfile.getId()
                                            )
                                    )
                            )

                            .map(customerProfileDetailMapper::toResponse
                            );
                })

                // -------------------------------------------------------------
                // 8. Generate MinIO presigned URLs
                // -------------------------------------------------------------
                .flatMap(this::toRoomResponseWithUrls)

                .doOnSuccess(response ->
                        log.info(
                                "Uploaded photos customerId={}, count={}",
                                customerId,
                                response.getProfile() == null
                                        || response.getProfile().photo_object_key() == null
                                        ? 0
                                        : response.getProfile()
                                        .photo_object_key()
                                        .size()
                        )
                )

                .doOnError(ex ->
                        log.error(
                                "Upload photos failed customerId={}, err={}",
                                customerId,
                                ex.getMessage(),
                                ex
                        )
                );
    }

    @Override
    public Mono<CustomerProfileResponse> getUserPhotoUrls(
            UUID customerId,
            UUID userId) {

        return customerProfileRepository.findById(customerId)

                .switchIfEmpty(
                        Mono.error(
                                new ProfileNotFoundException(customerId)
                        )
                )

                .flatMap(profile -> {

                    if (!Objects.equals(
                            profile.getUserAccountId(),
                            userId
                    )) {
                        return Mono.error(
                                new IllegalStateException(
                                        "You do not own this profile"
                                )
                        );
                    }

                    List<String> keys =
                            Optional.ofNullable(
                                    profile.getPhotoObjectKey()
                            ).orElse(List.of());

                    return Flux.fromIterable(keys)
                            .flatMap(
                                    key -> objectStorage.presignedGetUrl(
                                            key,
                                            PRESIGNED_URL_DURATION
                                    ),
                                    URL_CONCURRENCY
                            )
                            .collectList()
                            .map(urls ->
                                    new CustomerProfileResponse(
                                            profile.getId(),
                                            profile.getFirstName(),
                                            profile.getLastName(),
                                            profile.getGender(),
                                            profile.getNationalityNumber(),
                                            profile.getPassportNumber(),
                                            profile.getDateOfBirth(),
                                            null, // age - calculate if required
                                            profile.getEmail(),
                                            urls,
                                            keys,
                                            profile.getCreatedAt(),
                                            profile.getUpdatedAt()
                                    )
                            );
                });
    }

    @Override
    public Mono<CustomerProfileResponse> deleteUserPhoto(
            UUID customerId,
            UUID userId,
            String objectKey) {

        if (!StringUtils.hasText(objectKey)) {
            return Mono.error(
                    new IllegalArgumentException(
                            "Object key is required"
                    )
            );
        }

        return customerProfileRepository.findById(customerId)

                .switchIfEmpty(
                        Mono.error(
                                new ProfileNotFoundException(customerId)
                        )
                )

                .flatMap(profile -> {

                    if (!Objects.equals(
                            profile.getUserAccountId(),
                            userId
                    )) {
                        return Mono.error(
                                new IllegalStateException(
                                        "You do not own this profile"
                                )
                        );
                    }

                    List<String> currentKeys =
                            new ArrayList<>(
                                    Optional.ofNullable(
                                            profile.getPhotoObjectKey()
                                    ).orElse(List.of())
                            );

                    if (!currentKeys.contains(objectKey)) {
                        return Mono.error(
                                new IllegalArgumentException(
                                        "Photo does not belong to this profile"
                                )
                        );
                    }

                    currentKeys.remove(objectKey);

                    profile.setPhotoObjectKey(currentKeys);

                    return objectStorage
                            .removeObject(objectKey)

                            .then(
                                    customerProfileRepository.save(profile)
                            )

                            .flatMap(savedProfile -> {

                                List<String> keys =
                                        Optional.ofNullable(
                                                savedProfile
                                                        .getPhotoObjectKey()
                                        ).orElse(List.of());

                                return Flux.fromIterable(keys)
                                        .flatMap(
                                                key ->
                                                        objectStorage
                                                                .presignedGetUrl(
                                                                        key,
                                                                        PRESIGNED_URL_DURATION
                                                                ),
                                                URL_CONCURRENCY
                                        )
                                        .collectList()
                                        .map(urls ->
                                                new CustomerProfileResponse(
                                                        savedProfile.getId(),
                                                        savedProfile.getFirstName(),
                                                        savedProfile.getLastName(),
                                                        savedProfile.getGender(),
                                                        savedProfile.getNationalityNumber(),
                                                        savedProfile.getPassportNumber(),
                                                        savedProfile.getDateOfBirth(),
                                                        null,
                                                        savedProfile.getEmail(),
                                                        urls,
                                                        keys,
                                                        savedProfile.getCreatedAt(),
                                                        savedProfile.getUpdatedAt()
                                                )
                                        );
                            });
                });
    }

    // ========================================================================
    // Upload one file
    // ========================================================================

    private Mono<String> validateAndUploadOne(
            final UUID customerId,
            final FilePart file) {

        String contentType =
                file.headers().getContentType() != null
                        ? file.headers().getContentType().toString()
                        : null;

        if (!StringUtils.hasText(contentType)
                || !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            return Mono.error(
                    new IllegalArgumentException(
                            "Only jpg, png, webp are allowed"
                    )
            );
        }

        String extension =
                contentTypeToExt(contentType);

        String objectKey = "user/" + customerId + "/" + UUID.randomUUID() + extension;

        return writeToTempFile(file)

                .flatMap(tmp -> {

                    long maxBytes =
                            props.maxFileSizeBytes() == null
                                    ? DEFAULT_MAX_FILE_SIZE
                                    : props.maxFileSizeBytes();

                    return Mono.fromCallable(
                                    () -> Files.size(tmp)
                            )

                            .flatMap(size -> {

                                if (size > maxBytes) {

                                    return Mono.error(
                                            new IllegalArgumentException(
                                                    "File too large. Max="
                                                            + maxBytes
                                                            + " bytes"
                                            )
                                    );
                                }

                                return objectStorage
                                        .putObject(
                                                objectKey,
                                                contentType,
                                                tmp
                                        )
                                        .thenReturn(tmp);
                            })

                            .doFinally(signal ->
                                    safeDelete(tmp)
                            );
                })

                .thenReturn(objectKey);
    }

    // ========================================================================
    // Write Multipart FilePart to temporary file
    // ========================================================================

    private Mono<Path> writeToTempFile(
            final FilePart file) {

        return Mono.fromCallable(
                        () -> Files.createTempFile(
                                "user-photo-",
                                ".upload"
                        )
                )
                .flatMap(tmp ->
                        file.transferTo(tmp)
                                .thenReturn(tmp)
                );
    }

    // ========================================================================
    // Delete uploaded objects
    // ========================================================================

    private Mono<Void> removeObjects(
            List<String> objectKeys) {

        if (objectKeys == null || objectKeys.isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(objectKeys)
                .flatMap(
                        objectStorage::removeObject,
                        URL_CONCURRENCY
                )
                .then();
    }

    // ========================================================================
    // Delete temp file
    // ========================================================================

    private void safeDelete(
            final Path path) {

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            log.warn(
                    "Failed to delete temp file: {}",
                    path,
                    e
            );
        }
    }

    // ========================================================================
    // Content type -> extension
    // ========================================================================

    private String contentTypeToExt(
            final String contentType) {

        return switch (contentType) {

            case "image/jpeg" -> ".jpg";

            case "image/png" -> ".png";

            case "image/webp" -> ".webp";

            default -> "";
        };
    }

    // ========================================================================
    // CustomerProfileDetailResponse -> CustomerProfileDetailResponse
    // with presigned URLs
    // ========================================================================

    private Mono<CustomerProfileDetailResponse> toRoomResponseWithUrls(
            final CustomerProfileDetailResponse response) {

        if (response == null || response.getProfile() == null) {
            assert response != null;
            return Mono.just(response);
        }

        List<String> keys =
                Optional.ofNullable(
                        response.getProfile().photo_object_key()
                ).orElse(List.of());

        return Flux.fromIterable(keys)

                .flatMap(
                        key ->
                                objectStorage.presignedGetUrl(
                                        key,
                                        PRESIGNED_URL_DURATION
                                ),
                        URL_CONCURRENCY
                )

                .collectList()

                .map(urls -> {

                    CustomerProfileResponse profile =
                            response.getProfile();

                    CustomerProfileResponse updatedProfile =
                            new CustomerProfileResponse(
                                    profile.profileId(),
                                    profile.firstName(),
                                    profile.lastName(),
                                    profile.gender(),
                                    profile.nationalityNumber(),
                                    profile.passportNumber(),
                                    profile.dateOfBirth(),
                                    profile.age(),
                                    profile.email(),

                                    // Generated MinIO URLs
                                    urls,

                                    // Original MinIO object keys
                                    profile.photo_object_key(),

                                    profile.createdAt(),
                                    profile.updatedAt()
                            );

                    return new CustomerProfileDetailResponse(
                            response.getAccount(),
                            updatedProfile,
                            response.getAddress()
                    );
                });
    }
}