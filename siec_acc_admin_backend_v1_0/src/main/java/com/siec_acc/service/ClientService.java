package com.siec_acc.service;

import com.siec_acc.dto.request.ClientRequestDto;
import com.siec_acc.dto.response.ClientResponseDto;

import java.util.Map;

public interface ClientService {

    ClientResponseDto createClient(ClientRequestDto requestDto);

    ClientResponseDto updateClient(String id, ClientRequestDto requestDto);

    ClientResponseDto patchClient(String id, ClientRequestDto requestDto);

    void deleteClient(String id);

    ClientResponseDto getClientById(String id);

    /**
     * Returns { "customers": [...], "pagination": {...}, "meta": {...} }.
     * page (1-based) and size are optional — when both are null, every matching
     * client is returned on one page, same as before.
     */
    Map<String, Object> getAllClients(String status, String gst, String type, String search,
                                      Integer page, Integer size);

    Map<String, Object> getMeta();
}