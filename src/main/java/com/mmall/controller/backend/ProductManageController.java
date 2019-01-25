package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.FileService;
import com.mmall.service.ProductService;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Resource(name = "productService")
    private ProductService productService;
    @Resource(name = "fileService")
    private FileService fileService;

    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse addOrUpdateProduct(Product product) {
        return productService.addOrUpdateProduct(product);
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(Integer productId, Integer status) {
        return productService.updateStatus(productId, status);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVO> getDetail(Integer productId){
        return productService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return productService.getProductList(pageNum, pageSize);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(String productName, Integer productId,
                                           @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return productService.search(productName, productId, pageNum, pageSize);
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse<Map> upload(HttpServletRequest request, MultipartFile file){
        String localPath = request.getSession().getServletContext().getRealPath("upload");
        String fileName = fileService.upload(file,localPath);
        if(StringUtils.isBlank(fileName))
            return ServerResponse.fail("上传失败");
        Map<String, String> result = Maps.newHashMap();
        result.put("uri",fileName);
        result.put("url",PropertiesUtil.get("ftp.server.http.prefix")+fileName);
        return ServerResponse.successByData(result);
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map<String, Object> richtextImgUpload(HttpServletRequest request, HttpServletResponse response,
                                                 MultipartFile file){
        Map<String, Object> map = new HashMap<>();
        User user = (User) request.getSession().getAttribute(Const.CURRENT_USER);
        if(user == null){
            map.put("success", false);
            map.put("msg","用户需登陆");
            return map;
        }
        if(user.getRole()!=Const.Role.ROLE_ADMIN){
            map.put("success",false);
            map.put("msg","用户无权限");
            return map;
        }
        String localPath = request.getSession().getServletContext().getRealPath("upload");
        String resultStr = fileService.upload(file,localPath);
        if(StringUtils.isNotBlank(resultStr)){
            map.put("success",true);
            map.put("msg", "用户上传成功");
            map.put("file_path", PropertiesUtil.get("ftp.server.http.prefix")+resultStr);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return map;
        }else {
            map.put("success",false);
            map.put("msg", "用户上传失败");
            return map;
        }
    }

}
