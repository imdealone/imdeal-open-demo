package com.imdealone.open.demo.controller;

import com.imdealone.open.demo.config.ImdealConfiguration;
import com.imdealone.open.demo.utils.ImdealClient;
import com.imdealone.open.demo.vo.AuthRequest;
import com.imdealone.open.demo.vo.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 611 on 2025/5/8.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final ImdealConfiguration configuration;
    private final ImdealClient imdealClient;

    @GetMapping("/auth")
    public R<String> getToken() {
        AuthRequest.TokenResp tokenResp = imdealClient.executeWithoutToken(new AuthRequest());
        log.info("token：{}", tokenResp.getToken());
        return R.success(tokenResp.getToken());
    }
}
