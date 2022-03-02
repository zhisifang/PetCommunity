package life.pet.community.controller;

import life.pet.community.dto.AccessTockenDTO;
import life.pet.community.dto.GithubUser;
import life.pet.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

//登录成功后返回主页面
@Controller
public class AuthorizeController {
    //@Autowired将spring容器中实例化的实例加载到当前使用的上下文
    @Autowired
    private GithubProvider githubProvider;

    //@Value回去配置文件中读取同名key的值，并自动赋值
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.screct}")
    private String clientSecret;
    @Value("${github.redirect.url}")
    private String redirectUrl;

    //RequestParam请求参数
    //参数要求见：https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps 第一项“请求用户github身份”之参数列表
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request) {
        AccessTockenDTO accessTockenDTO = new AccessTockenDTO();
        accessTockenDTO.setClient_id(clientId);
        accessTockenDTO.setClient_secret(clientSecret);
        accessTockenDTO.setCode(code);
        accessTockenDTO.setRedirect_url(redirectUrl);
        accessTockenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTockenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        if (githubUser != null){
            //登录成功，写session和cookie
            request.getSession().setAttribute("user", githubUser);
            return "redirect:index";
        } else{
            //登陆失败，重新登录

        }
        return "index";
    }
}
