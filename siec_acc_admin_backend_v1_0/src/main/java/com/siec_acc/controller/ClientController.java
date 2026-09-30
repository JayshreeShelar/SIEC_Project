package com.siec_acc.controller;

import com.siec_acc.dto.request.ClientRequestDto;
import com.siec_acc.dto.response.ClientResponseDto;
import com.siec_acc.exceptions.ApiResponse;
import com.siec_acc.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients/v1")
public class ClientController {

    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/create-client")
    public ResponseEntity<ApiResponse<ClientResponseDto>> createClient(@RequestBody ClientRequestDto requestDto) {
        logger.info("API HIT: POST /create-client | name={}", requestDto.getName());
        ClientResponseDto response = clientService.createClient(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer created successfully.", response));
    }

    @PutMapping("/update-client/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDto>> updateClient(
            @PathVariable String id, @RequestBody ClientRequestDto requestDto) {
        logger.info("API HIT: PUT /update-client/{}", id);
        ClientResponseDto response = clientService.updateClient(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully.", response));
    }

    @PatchMapping("/patch-client/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDto>> patchClient(
            @PathVariable String id, @RequestBody ClientRequestDto requestDto) {
        logger.info("API HIT: PATCH /patch-client/{}", id);
        ClientResponseDto response = clientService.patchClient(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully.", response));
    }

    @DeleteMapping("/delete-client/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable String id) {
        logger.info("API HIT: DELETE /delete-client/{}", id);
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully.", null));
    }

    @GetMapping("/get-client/{id}")
    public ResponseEntity<ApiResponse<ClientResponseDto>> getClient(@PathVariable String id) {
        logger.info("API HIT: GET /get-client/{}", id);
        ClientResponseDto response = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer fetched successfully.", response));
    }

    @GetMapping("/get-all-clients")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllClients(
            @RequestParam(required = false, defaultValue = "all") String status,
            @RequestParam(required = false, defaultValue = "all") String gst,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        logger.info("API HIT: GET /get-all-clients | status={} gst={} type={} search={} page={} size={}",
                status, gst, type, search, page, size);
        Map<String, Object> response = clientService.getAllClients(status, gst, type, search, page, size);
        return ResponseEntity.ok(ApiResponse.success("Customers fetched successfully.", response));
    }

    @GetMapping("/get-meta")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMeta() {
        logger.info("API HIT: GET /get-meta");
        Map<String, Object> response = clientService.getMeta();
        return ResponseEntity.ok(ApiResponse.success("Meta fetched successfully.", response));
    }

    // Dropdown values for "Client Type" (form + "All Types" filter)
    @GetMapping("/get-client-types")
    public ResponseEntity<ApiResponse<List<String>>> getClientTypes() {
        logger.info("API HIT: GET /get-client-types");
        List<String> response = clientService.getClientTypes();
        return ResponseEntity.ok(ApiResponse.success("Client types fetched successfully.", response));
    }
}