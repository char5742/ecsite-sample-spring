package com.example.ec_2024b_back.share.infrastructure.api;

import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

/** カスタムエラー属性を提供するクラス. デフォルトのエラー属性を拡張します。 */
@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);

    // 必要に応じてカスタム属性を追加
    errorAttributes.put("application", "EC Site Application");

    return errorAttributes;
  }
}
