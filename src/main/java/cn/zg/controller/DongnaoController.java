package cn.zg.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zg.annotation.Autowired;
import cn.zg.annotation.Controller;
import cn.zg.annotation.RequestMapping;
import cn.zg.service.DongnaoService;
import cn.zg.service.MyService;

/** 
* @author 作者 zg
* @version 创建时间：2018年12月22日 下午8:53:53 
*/

@Controller("/dongnao")
public class DongnaoController {
    
	 @Autowired("myServiceimpl")
	 private MyService myService;
	 @Autowired("dongnaoServiceimpl")
	 private DongnaoService dnService;
	 
	 
	 @RequestMapping("/insert")
	 public String insert(HttpServletRequest request,HttpServletResponse response,String insert) {
		 System.out.println(request.getRequestURI()+":insert");
		 myService.insert(null);
		 dnService.insert(null);
		 return null;
	 }
	 
}
