package com.ipeaksoft.moneyday.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ipeaksoft.moneyday.admin.util.security.SpringSecurityUtils;
import com.ipeaksoft.moneyday.core.entity.AdminUser;
import com.ipeaksoft.moneyday.core.service.AdminUserService;

public class BaseController {
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	// 获得所有设备列表
	public String devices = "%s/dms/devices";
	// 得到所有的脚本列表
	public String scripts = "%s/dms/scripts";
	// 得到某个游戏有哪些脚本可以跑
	public String game_scripts = "%s/dms/game_scripts?id=%s&version=%s&platform=%s";
	// 重启一个设备
	public String reboot_device = "%s/dms/reboot_device?udid=%s";
	// 执行脚本
	public String run_script = "%s/dms/run_script"
			+ "?udid=%s&internal_id=%s&script_id=%s&game_id=%s"
			+ "&username=%s&password=%s&server=%s&time=%s&recover=";
	//强行停止当前脚本
	public String stop_script="%s/dms/stop_script?udid=%s";
	//强行停止所有脚本
	public String stop_all_scripts = "%s/dms/stop_all_scripts";
	//暂停当前脚本
	public String pause_script = "%s/dms/pause_script?udid=%s";
	//继续当前脚本
	public String resume_script = "%s/dms/resume_script?udid=%s";

	
	@Autowired
	protected AdminUserService adminUserService;

	// 取得登录用户
	public AdminUser getUser() {
		String name = SpringSecurityUtils.getCurrentUserName();
		AdminUser user = adminUserService.getUserByName(name);
		return user;
	}
}
