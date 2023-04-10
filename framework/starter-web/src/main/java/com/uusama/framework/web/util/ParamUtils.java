package com.uusama.framework.web.util;

import com.uusama.framework.api.constants.GlobalErrorCodeConstants;
import com.uusama.framework.web.exception.ServiceException;

/**
 * 参数检查相关工具
 * @author uusama
 */
public class ParamUtils {

    public static void notNull(Object object, String errorMessage) {
        if (object == null) {
            throw new ServiceException(GlobalErrorCodeConstants.PARAM_INVALID);
        }
    }
}
