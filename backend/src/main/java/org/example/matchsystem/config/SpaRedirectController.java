
package org.example.matchsystem.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 处理 Vue 路由转发的控制器
 * 作用：当用户刷新 /match, /player 等页面时，强制转发回 index.html，让 Vue 自己去处理路由
 */
@Controller
public class SpaRedirectController {

    // 匹配所有不带后缀名（如 .js, .css, .png）的路径
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        // 转发到 static/index.html
        return "forward:/index.html";
    }
}