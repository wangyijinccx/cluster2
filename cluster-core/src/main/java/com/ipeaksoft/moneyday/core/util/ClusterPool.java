package com.ipeaksoft.moneyday.core.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;

import com.ipeaksoft.moneyday.core.entity.ClusterGameAccount;
import com.ipeaksoft.moneyday.core.service.ClusterGameAccountService;

public class ClusterPool {

	public static ClusterPool pool = null;

	private Queue<ClusterGameAccount> queue = null;

	@Autowired
	private ClusterGameAccountService clusterGameAccountService;

	// 私有化
	private ClusterPool() {
		queue = new ArrayDeque<ClusterGameAccount>(10000);
	}

	/**
	 * <p>
	 * 获取池实例
	 * </p>
	 * 
	 * @return
	 */
	public static ClusterPool getInstance() {
		if (pool == null) {
			synchronized (ClusterPool.class) {
				if (pool == null) {
					pool = new ClusterPool();
				}
			}
		}
		return pool;
	}

	/**
	 * <p>
	 * 添加
	 * </p>
	 * 
	 * @param result
	 */
	public void add(ClusterGameAccount result) {
		synchronized (queue) {
			this.queue.add(result);
		}
	}

	/**
	 * <p>
	 * 当前池数据量大小
	 * </p>
	 * 
	 * @return
	 */
	public int getPoolSize() {
		synchronized (queue) {
			return queue.size();
		}
	}

	/**
	 * <p>
	 * 每次获取批量的点击数据
	 * </p>
	 * 
	 * @return
	 */
	public List<ClusterGameAccount> getBattchList() {
		synchronized (queue) {
			int size = queue.size();
			List<ClusterGameAccount> crList = new ArrayList<ClusterGameAccount>();
			for (int i = 1; i <= size; i++) {
				crList.add(this.queue.poll());
			}
			return crList;
		}
	}

	/**
	 * <p>
	 * 每次获取批量的点击数据,并有异常处理机制
	 * </p>
	 * 
	 * @return
	 */
	public List<ClusterGameAccount> getBattchListAndExc() {
		synchronized (queue) {
			int size = queue.size();
			List<ClusterGameAccount> crList = new ArrayList<ClusterGameAccount>();
			for (int i = 1; i <= size; i++) {
				// 判断账号状态，如果强制异常处理，就不加入队列，并修改状态为1
				ClusterGameAccount clusterGameAccount = this.queue.poll();
				Integer taskid = clusterGameAccount.getId();
				ClusterGameAccount cg = clusterGameAccountService
						.selectByPrimaryKey(taskid);
				if ("6".equals(cg.getStatus())) {
					ClusterGameAccount model = new ClusterGameAccount();
					model.setId(taskid);
					model.setStatus("1");
					clusterGameAccountService
							.updateByPrimaryKeySelective(model);
				} else {
					crList.add(clusterGameAccount);
				}
			}
			return crList;
		}
	}
}
