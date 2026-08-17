package com.wen.controller;

import com.wen.common.response.Response;
import com.wen.model.dto.AnchorDto;
import com.wen.service.AnchorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : rjw
 * @date : 2026-04-08
 */
@RestController
@RequestMapping("/anchor")
@RequiredArgsConstructor
public class AnchorController {

    private final AnchorService anchorService;

    @PostMapping("/register")
    public Response<String> registerAnchor(@RequestBody AnchorDto anchorDto) {
        String response = anchorService.registerAnchor(anchorDto);
        return Response.success(response);
    }

    @PostMapping("/query")
    public Response<List<AnchorDto>> queryAnchor() {
        List<AnchorDto> response = anchorService.queryAnchor();
        return Response.success(response);
    }



}
