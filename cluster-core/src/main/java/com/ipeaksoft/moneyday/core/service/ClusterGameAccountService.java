package com.ipeaksoft.moneyday.core.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ipeaksoft.moneyday.core.entity.ClusterAccountUdsc;
import com.ipeaksoft.moneyday.core.entity.ClusterDms;
import com.ipeaksoft.moneyday.core.entity.ClusterGame;
import com.ipeaksoft.moneyday.core.entity.ClusterGameAccount;
import com.ipeaksoft.moneyday.core.mapper.ClusterGameAccountMapper;
import com.ipeaksoft.moneyday.core.util.ClusterPool;
import com.ipeaksoft.moneyday.core.util.TestDeom;

@Service
public class ClusterGameAccountService extends BaseService {

	@Autowired
	private ClusterGameAccountMapper clusterGameAccountMapper;
	@Autowired
	private HttpService httpService;
	@Autowired
	private ClusterAccountUdscService clusterAccountUdscService;
	@Autowired
	private ClusterGameService clusterGameService;
	@Autowired
	private ClusterDmsService clusterDmsService;

	public int insertSelective(ClusterGameAccount record) {
		return clusterGameAccountMapper.insertSelective(record);
	}

	public List<Map<String, Object>> selectListGameAccont(Integer start,
			Integer length) {
		return clusterGameAccountMapper.selectListGameAccont(start, length);
	}

	public int selectNum() {
		return clusterGameAccountMapper.selectNum();
	}

	public List<ClusterGameAccount> selectByGameId(Integer id, Integer start,
			Integer length) {
		return clusterGameAccountMapper.selectByGameId(id, start, length);
	}

	public int selectByGameIdNum(Integer id) {
		return clusterGameAccountMapper.selectByGameIdNum(id);
	}

	public ClusterGameAccount selectByPrimaryKey(Integer id) {
		return clusterGameAccountMapper.selectByPrimaryKey(id);
	}

	public int updateByPrimaryKeySelective(ClusterGameAccount record) {
		return clusterGameAccountMapper.updateByPrimaryKeySelective(record);
	}

	public List<Map<String, Object>> selectAccount(Integer id) {
		return clusterGameAccountMapper.selectAccount(id);
	}

	public int insertGameAccount(List<ClusterGameAccount> list) {
		return clusterGameAccountMapper.insertGameAccount(list);
	}

	@SuppressWarnings("unused")
	public void daTask() {
		Integer taskId;
		String udid = "";
		String script_id = "";
		String username;
		String password;
		String server;
		Integer time;
		String internal_id = "";
		ClusterDms dms = null;
		Boolean b = true;
		List<ClusterDms> lists = clusterDmsService.selectAll();
		if (lists.size() > 0) {
			ClusterPool clusterPool = ClusterPool.getInstance();
			List<ClusterGameAccount> list = clusterPool.getBattchList();
			logger.info("adcluster-list:{}", list.toString());
			if (list != null && list.size() > 0) {
				for (ClusterGameAccount clusterGameAccount : list) {
					taskId = clusterGameAccount.getId();
					username = clusterGameAccount.getAccount();
					password = clusterGameAccount.getPasswd();
					server = clusterGameAccount.getGameServer();
					time = (clusterGameAccount.getRunTime()) * 60;

					// 匹配设备 所有pc的所有设备
					w: for (ClusterDms clusterDms : lists) {
						Integer dmsId = clusterDms.getId();
						String url = clusterDms.getAvailableUrl();
						url = String.format(devices, url);
						String callback = httpService.get(url);
						// callback = TestDeom.getCall();
						JSONObject json = JSONObject.parseObject(callback);

						if (null == json
								|| (null != json.getString("errCode") && !"0"
										.equals(json.getString("errCode")))) {
							//
							// clusterPool.add(clusterGameAccount);
							// continue;
						} else {
							JSONArray devices = json.getJSONArray("devices");
							n: for (int i = 0; i < devices.size(); i++) {
								JSONObject item = devices.getJSONObject(i);
								String state = item.getString("state");
								if (null != state && "FREE".equals(state)) {
									dms = clusterDms;
									udid = item.getString("udid");
									internal_id = item.getString("internalId");
									b = false;
									lists.remove(clusterDms);
									break w;
								}
							}
						}
					}

					// 如果没有空闲设备
					if (b) {
						clusterPool.add(clusterGameAccount);
						continue;
					} else {
						b = true;
					}

					// 匹配脚本
					// 获取bundleId gameid
					Integer gameid = clusterGameAccount.getGameId();
					ClusterGame clusterGame = clusterGameService
							.selectByPrimaryKey(gameid);

					String url_scr = String
							.format(game_scripts, dms.getAvailableUrl(),
									clusterGame.getBundleid(),
									clusterGame.getVersion(),
									clusterGame.getPlatform());

					logger.info("adcluster-url_scr:{}", url_scr);
					String callback_scr = httpService.get(url_scr);

					// callback_scr = TestDeom.getScr();

					JSONObject json_scr = JSONObject.parseObject(callback_scr);
					if (null == json_scr
							|| (null != json_scr.getString("errCode") && !"0"
									.equals(json_scr.getString("errCode")))) {
						// 接口异常
						clusterPool.add(clusterGameAccount);
						continue;
					} else {
						JSONArray scrs = json_scr.getJSONArray("scripts");
						for (int i = 0; i < scrs.size(); i++) {
							// 现在只有一个脚本
							JSONObject item = scrs.getJSONObject(i);
							script_id = item.getString("id");
							break;
						}
					}

					String url_do = String.format(run_script, dms.getAvailableUrl(),
							udid, internal_id, script_id,
							clusterGame.getBundleid(), username, password,
							server, time);
					// 执行任务

					logger.info("adcluster-url_do:{}", url_do);
					String doback = httpService.get(url_do);

					// doback = "{\"errCode\" : 0 ,\"errMsg\" : \"xxx\" }";

					JSONObject json_doback = JSONObject.parseObject(doback);
					if (null == json_doback
							|| (null != json_doback.getString("errCode") && !"0"
									.equals(json_doback.getString("errCode")))) {
						// 接口异常
						clusterPool.add(clusterGameAccount);
						continue;
					}
					// 脚本执行成功，再做记录
					// 保存taskid udid scrid ，如果taskid已存在，则更新 ，每个taskdi只有一条记录
					List<ClusterAccountUdsc> clusterAccountUdscs = clusterAccountUdscService
							.selectByTaskId(taskId);
					if (clusterAccountUdscs.size() > 0) {
						ClusterAccountUdsc clusterAccountUdsc = clusterAccountUdscs
								.get(0);
						clusterAccountUdsc.setTaskid(taskId);
						clusterAccountUdsc.setUdid(udid);
						clusterAccountUdsc.setInternalId(internal_id);
						clusterAccountUdsc.setScriptsId(script_id);
						clusterAccountUdsc.setCreateTime(new Date());
						clusterAccountUdsc.setModifyTime(new Date());
						clusterAccountUdsc.setDmsid(dms.getId());
						if (clusterAccountUdscService
								.updateByPrimaryKeySelective(clusterAccountUdsc) < 1) {
							// clusterPool.add(clusterGameAccount);
							// continue;
						}
					} else {
						ClusterAccountUdsc clusterAccountUdsc = new ClusterAccountUdsc();
						clusterAccountUdsc.setTaskid(taskId);
						clusterAccountUdsc.setUdid(udid);
						clusterAccountUdsc.setInternalId(internal_id);
						clusterAccountUdsc.setScriptsId(script_id);
						clusterAccountUdsc.setCreateTime(new Date());
						clusterAccountUdsc.setModifyTime(new Date());
						clusterAccountUdsc.setDmsid(dms.getId());
						if (clusterAccountUdscService
								.insertSelective(clusterAccountUdsc) < 1) {
							// clusterPool.add(clusterGameAccount);
							// continue;
						}
					}

					// 更改状态
					ClusterGameAccount model = new ClusterGameAccount();
					model.setId(taskId);
					model.setStatus("2");
					if (this.updateByPrimaryKeySelective(model) < 1) {
						// clusterPool.add(clusterGameAccount);
						// continue;
					}
					lists = clusterDmsService.selectAll();
				}

			}
		}
	}

	public int updateStatus() {
		return clusterGameAccountMapper.updateStatus();
	}

	public List<ClusterGameAccount> selectByIds(List<Integer> ids) {
		return clusterGameAccountMapper.selectByIds(ids);
	}

	public Map<String, Object> selectGames(String gameid, String account,
			String server) {
		return clusterGameAccountMapper.selectGames(gameid, account, server);
	}

	public List<ClusterGameAccount> checkGames(Integer gameid, String account,
			String server) {
		return clusterGameAccountMapper.checkGames(gameid, account, server);
	}

	public int updateByDms(Integer dmsid) {
		return clusterGameAccountMapper.updateByDms(dmsid);
	}

	@SuppressWarnings("unused")
	public void daTask2() {
		Integer taskId;
		String udid = "";
		String script_id = "";
		String username;
		String password;
		String server;
		Integer time;
		String internal_id = "";
		ClusterDms dms = null;
		List<ClusterDms> lists = clusterDmsService.selectAll();
		if (lists.size() > 0) {
			ClusterPool clusterPool = ClusterPool.getInstance();
			List<ClusterGameAccount> list = clusterPool.getBattchList();
			logger.info("adcluster-list:{}", list.toString());
			if (list != null && list.size() > 0) {
				for (ClusterGameAccount clusterGameAccount : list) {
					taskId = clusterGameAccount.getId();
					username = clusterGameAccount.getAccount();
					password = clusterGameAccount.getPasswd();
					server = clusterGameAccount.getGameServer();
					time = (clusterGameAccount.getRunTime()) * 60;

					// 匹配设备
					// 所有pc的所有设备，为防止监控端异常，遇到任务请求问题，都重新加入队列，等待下次执行。防止一直循环，不释放锁
					w: for (ClusterDms clusterDms : lists) {
						Integer dmsId = clusterDms.getId();
						String url = clusterDms.getAvailableUrl();
						// pc下的所有设备
						url = String.format(devices, url);
						String callback = httpService.get(url);

						// callback = TestDeom.getCall();

						JSONObject json = JSONObject.parseObject(callback);

						// 获取设备异常，则查看下一台pc
						if (null == json
								|| (null != json.getString("errCode") && !"0"
										.equals(json.getString("errCode")))) {
						} else {
							JSONArray devices = json.getJSONArray("devices");
							n: for (int i = 0; i < devices.size(); i++) {
								JSONObject item = devices.getJSONObject(i);
								String state = item.getString("state");
								if (null != state && "FREE".equals(state)) {
									dms = clusterDms;
									udid = item.getString("udid");
									internal_id = item.getString("internalId");

									// 匹配执行游戏下的脚本
									// 获取bundleId gameid
									Integer gameid = clusterGameAccount
											.getGameId();
									ClusterGame clusterGame = clusterGameService
											.selectByPrimaryKey(gameid);

									String url_scr = String.format(
											game_scripts, dms.getAvailableUrl(),
											clusterGame.getBundleid(),
											clusterGame.getVersion(),
											clusterGame.getPlatform());

									logger.info("adcluster-url_scr:{}", url_scr);
									String callback_scr = httpService
											.get(url_scr);

									// callback_scr = TestDeom.getScr();

									JSONObject json_scr = JSONObject
											.parseObject(callback_scr);
									// 如果没有获取到脚本，或者获取脚本失败，那就不执行这个任务了，放在下次执行。
									// 虽然最后判了状态，但双重保险，防止数据丢失
									// 因为最后加了验证状态，break n更好些
									if (null == json_scr
											|| (null != json_scr
													.getString("errCode") && !"0"
													.equals(json_scr
															.getString("errCode")))) {
										// 接口异常
										clusterPool.add(clusterGameAccount);
										break w;
									} else {
										JSONArray scrs = json_scr
												.getJSONArray("scripts");
										for (int m = 0; m < scrs.size(); m++) {
											// 现在只有一个脚本
											JSONObject item_src = scrs
													.getJSONObject(m);
											script_id = item_src
													.getString("id");
											break;
										}
									}
									String url_do = String.format(run_script,
											dms.getAvailableUrl(), udid, internal_id,
											script_id,
											clusterGame.getBundleid(),
											username, password, server, time);
									// 执行任务

									logger.info("adcluster-url_do:{}", url_do);
									String doback = httpService.get(url_do);

									// doback =
									// "{\"errCode\" : 0 ,\"errMsg\" : \"xxx\" }";

									JSONObject json_doback = JSONObject
											.parseObject(doback);
									// 如果脚本执行异常，那就不执行这个任务了，放在下次执行
									if (null == json_doback
											|| (null != json_doback
													.getString("errCode") && !"0"
													.equals(json_doback
															.getString("errCode")))) {
										// 接口异常
										clusterPool.add(clusterGameAccount);
										break w;
									}
									// 脚本执行成功，再做记录
									// 保存taskid udid scrid ，如果taskid已存在，则更新
									// ，每个taskdi只有一条记录
									List<ClusterAccountUdsc> clusterAccountUdscs = clusterAccountUdscService
											.selectByTaskId(taskId);
									if (clusterAccountUdscs.size() > 0) {
										ClusterAccountUdsc clusterAccountUdsc = clusterAccountUdscs
												.get(0);
										clusterAccountUdsc.setTaskid(taskId);
										clusterAccountUdsc.setUdid(udid);
										clusterAccountUdsc
												.setInternalId(internal_id);
										clusterAccountUdsc
												.setScriptsId(script_id);
										clusterAccountUdsc
												.setModifyTime(new Date());
										clusterAccountUdsc
												.setDmsid(dms.getId());
										clusterAccountUdscService
												.updateByPrimaryKeySelective(clusterAccountUdsc);
									} else {
										ClusterAccountUdsc clusterAccountUdsc = new ClusterAccountUdsc();
										clusterAccountUdsc.setTaskid(taskId);
										clusterAccountUdsc.setUdid(udid);
										clusterAccountUdsc
												.setInternalId(internal_id);
										clusterAccountUdsc
												.setScriptsId(script_id);
										clusterAccountUdsc
												.setCreateTime(new Date());
										clusterAccountUdsc
												.setModifyTime(new Date());
										clusterAccountUdsc
												.setDmsid(dms.getId());
										clusterAccountUdscService
												.insertSelective(clusterAccountUdsc);
									}

									// 更改状态
									ClusterGameAccount model = new ClusterGameAccount();
									model.setId(taskId);
									model.setStatus("2");
									this.updateByPrimaryKeySelective(model);
									break w;
								}
							}
						}
					}
					
					//如果匹配的设备都不合适，则重新加入到队列
					ClusterGameAccount clusterGA = this.selectByPrimaryKey(taskId);
					if("5".equals(clusterGA.getStatus())){
						clusterPool.add(clusterGameAccount);
					}else if("6".equals(clusterGA.getStatus())){
						//如果强制异常处理
						ClusterGameAccount model = new ClusterGameAccount();
						model.setId(taskId);
						model.setStatus("1");
						this.updateByPrimaryKeySelective(model);
					}
				}
			}
		}
	}
}
