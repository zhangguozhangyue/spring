package cn.zg.service.impl;

import java.util.Map;

import cn.zg.annotation.Service;
import cn.zg.service.DongnaoService;

/** 
* @author 作者 zg
* @version 创建时间：2018年12月22日 下午8:59:09 
*/
@Service("dongnaoServiceimpl")
public class DongnaoServiceimpl implements DongnaoService {

	@Override
	public int insert(Map map) {
		System.out.println("dongnaoServiceimpl"+":insert");
		return 0;
	}

	@Override
	public int delelte(Map map) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(Map map) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int select(Map map) {
		// TODO Auto-generated method stub
		return 0;
	}

}
