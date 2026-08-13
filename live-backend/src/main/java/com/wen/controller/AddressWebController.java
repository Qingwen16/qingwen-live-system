package com.wen.controller;

import com.wen.common.annotation.RequireRole;
import com.wen.common.enums.RoleTypeEnum;
import com.wen.common.response.PageResult;
import com.wen.common.response.Response;
import com.wen.model.entity.AddressEntity;
import com.wen.model.vo.AddressQueryRequest;
import com.wen.service.AddressWebService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 后台地址管理控制器（Web 管理端）
 *
 * @author : rjw
 * @date : 2026-04-09
 */
@RestController
@RequestMapping("/web/address")
@RequiredArgsConstructor
@RequireRole({RoleTypeEnum.ADMIN, RoleTypeEnum.SUPER_ADMIN})
public class AddressWebController {

    private final AddressWebService addressWebService;

    /**
     * 分页查询地址
     */
    @PostMapping("/query")
    public Response<PageResult<AddressEntity>> query(@RequestBody AddressQueryRequest request) {
        return Response.success(addressWebService.pageQuery(request));
    }

    /**
     * 删除地址（管理端强制删除）
     */
    @PostMapping("/delete")
    public Response<Void> delete(@RequestParam Long id) {
        addressWebService.deleteAddress(id);
        return Response.success(null, "地址已删除");
    }
}
