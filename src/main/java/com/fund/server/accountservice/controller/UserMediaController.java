package com.fund.server.accountservice.controller;


import com.fund.server.accountservice.dto.CustomerProfileDetailResponse;
import com.fund.server.accountservice.service.UserMediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/customers")
public class UserMediaController {

    private final UserMediaService userMediaService;

    @PostMapping(value = "/{id}/{customerId}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Mono<CustomerProfileDetailResponse> uploadPhoto(
            @PathVariable final UUID id,
            @PathVariable UUID customerId,
            @RequestPart("files") final Flux<FilePart> files
    ){
        return userMediaService.uploadUserPhoto(id, customerId, files);
    }

    /*@GetMapping("/{id}/photos")
    public Mono<CustomerProfileDetailResponse> getPhotos(@PathVariable final UUID id, @RequestBody UUID customerId ){
        return  userMediaService.getUserPhotoUrls(id, customerId);
    }

    @DeleteMapping("/{id}/photos")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CustomerProfileDetailResponse> deletePhoto(@PathVariable final String id, @RequestParam("objectKey") final String objectKey){

        if(objectKey == null || objectKey.isBlank()){
            return Mono.error(new ServerWebInputException("objectKey is required"));
        }
        return currentOwnerService.getCurrentOwnerId()
                .flatMap(ownerId -> roomMediaService.deleteRoomPhoto(id, ownerId, objectKey));
    }*/
}
