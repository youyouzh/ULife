package com.uusama.framework.web.util;

import com.uusama.framework.api.constants.GlobalErrorCodeConstants;
import com.uusama.framework.api.pojo.ErrorCode;
import com.uusama.framework.web.enums.CommonState;
import com.uusama.framework.web.exception.ServiceExceptionUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 参数检查相关工具
 * @author uusama
 */
@Slf4j
@UtilityClass
public class ParamUtils {

    public static void checkMatch(boolean condition) {
        if (!condition) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.PARAM_INVALID);
        }
    }

    public static void checkMatch(boolean condition, ErrorCode errorCode) {
        if (!condition) {
            throw ServiceExceptionUtil.exception(errorCode);
        }
    }

    public static void checkMatch(boolean condition, ErrorCode errorCode, String errorMessage) {
        if (!condition) {
            throw ServiceExceptionUtil.exception(errorCode.withMessage(errorMessage));
        }
    }

    public static void checkNotMatch(boolean condition, ErrorCode errorCode) {
        if (condition) {
            throw ServiceExceptionUtil.exception(errorCode);
        }
    }

    public static void checkNotNull(Object object, String errorMessage) {
        checkMatch(object != null);
    }

    public static void checkNotNull(Object object, ErrorCode errorCode) {
        checkMatch(object != null, errorCode);
    }

    public static void checkNotNull(Object object, ErrorCode errorCode, String errorMessage) {
        checkMatch(object != null, errorCode, errorMessage);
    }

    public static void checkEnable(CommonState state, ErrorCode errorCode) {
        checkMatch(state.isEnable(), errorCode);
    }
}
