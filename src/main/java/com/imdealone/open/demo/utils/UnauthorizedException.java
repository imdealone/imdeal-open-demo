package com.imdealone.open.demo.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 611 on 2025/5/8.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UnauthorizedException extends RuntimeException{
}
