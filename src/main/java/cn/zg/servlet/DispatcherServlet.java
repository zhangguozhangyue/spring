package cn.zg.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.zg.annotation.Autowired;
import cn.zg.annotation.Controller;
import cn.zg.annotation.RequestMapping;
import cn.zg.annotation.Service;
import cn.zg.controller.DongnaoController;

/** 
* @author 作者 zg
* @version 创建时间：2018年12月22日 下午5:05:53 
*/
public class DispatcherServlet extends HttpServlet{
	
     
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Properties properties = new Properties();
	
	private List<String> packageNames=new ArrayList<String>();
	
	private Map<String,Object> instanceMap=new HashMap<String,Object>();
	
	private Map<String,Object> handlerMap=new HashMap<String,Object>();

	public void init(ServletConfig config) throws ServletException {
		doLoadConfig(config.getInitParameter("contextConfigLocation"));
		scanPackage(properties.getProperty("scanPackage"));
			try {
				filterAndInstance();
					
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			handlerMap();
			try {
				ioc();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	}
	
	private void  doLoadConfig(String location){

        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);
        try {
           System.out.println("读取"+location+"里面的文件");
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关流
            if(null!=resourceAsStream){
                try {
                    resourceAsStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

	
	
	
	private void handlerMap() {
		if(instanceMap.size()<=0) {
			return;
		}
		for(Map.Entry<String, Object> entry:instanceMap.entrySet()) {
			
		  if(entry.getValue().getClass().isAnnotationPresent(Controller.class)) {
			  Controller controller=entry.getValue().getClass().getAnnotation(Controller.class);
			  String ctvalue=controller.value();
			  Method[] methods=entry.getValue().getClass().getMethods();
			  for(Method method:methods) {
				  if(method.isAnnotationPresent(RequestMapping.class)) {
					  RequestMapping rm=(RequestMapping)method.getAnnotation(RequestMapping.class);
					  
					  String rmvalue=rm.value();
					  
					  handlerMap.put(ctvalue+rmvalue, method);
				  }else {
					  continue;
				  }
			  }
		  }else {
			  continue;
		  }
		 
		}
	}
     
	private void ioc() throws Exception {
		
		if(instanceMap.size()<=0) {
			return;
		}
		for(Map.Entry<String, Object> entry:instanceMap.entrySet()) {
			Field[] fields=entry.getValue().getClass().getDeclaredFields();
			for(Field field:fields) {
				field.setAccessible(true);
				if(field.isAnnotationPresent(Autowired.class)) {
					  Autowired qualifier=(Autowired)field.getAnnotation(Autowired.class);
					  String key=qualifier.value();
					  instanceMap.get(key);
					  field.setAccessible(true);
					  field.set(entry.getValue(), instanceMap.get(key));
				}
			}
		}
		
	}
	
	
	private void filterAndInstance() throws Exception {
		
		if(packageNames.size()<=0) {
			return;
		}
		
		for(String className:packageNames) {
			Class ccName=Class.forName(className.replace(".class", ""));
			if(ccName.isAnnotationPresent(Controller.class)) {
				Object instance=ccName.newInstance();
				Controller an=(Controller)ccName.getAnnotation(Controller.class);
				String key=an.value();
				instanceMap.put(key,instance);
			}else if(ccName.isAnnotationPresent(Service.class)) {
				Object instance=ccName.newInstance();
				Service an=(Service)ccName.getAnnotation(Service.class);
				String key=an.value();
				instanceMap.put(key,instance);
			}else {
				continue;
			}
		}
		
	}
	private void scanPackage(String basePackage) {
		URL url=this.getClass().getClassLoader().getResource("/"+replace(basePackage));
		System.out.println(url);
		String pathFile=url.getFile();
		System.out.println(pathFile);
		File file=new File(pathFile);
		System.out.println(file);
		String[] files=file.list();
		
		for(String path:files) {
			File eachFile=new File(pathFile+path);
			System.out.println(pathFile+path);
			if(eachFile.isDirectory()) {
				scanPackage(basePackage+"."+eachFile.getName());
			}else {
				System.out.println(basePackage+"."+eachFile.getName());
				packageNames.add(basePackage+"."+eachFile.getName());
			}
			
		}
	}
	
	private String replace(String path) {
		return path.replaceAll("\\.", "/");
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri=request.getRequestURI();
		
		String context=request.getContextPath();
		
		String path=uri.replace(context, "");
		
		Method method=(Method)handlerMap.get(path);
		
		DongnaoController controller=(DongnaoController)instanceMap.get(path.split("/")[1]);
		try {
			method.invoke(controller,new Object[] {request,response,null});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		}
} 
