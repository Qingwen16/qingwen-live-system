package com.wen.module.user.controller;

import com.wen.common.response.Response;
import com.wen.module.user.domain.vo.AnchorInfoDto;
import com.wen.module.user.service.AnchorService;
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
    public Response<String> registerAnchor(@RequestBody AnchorInfoDto anchorInfoDto) {
        String response = anchorService.registerAnchor(anchorInfoDto);
        return Response.success(response);
    }

    @GetMapping("/query")
    public Response<List<AnchorInfoDto>> queryAnchor() {
        List<AnchorInfoDto> response = anchorService.queryAnchor();
        return Response.success(response);
    }



}
