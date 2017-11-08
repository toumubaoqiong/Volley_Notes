package com.vince.networkservice.request;


/**
 * @description  用于判断服务器返回数据是否为空
 */
public interface IDataEmpty {
	
	/**
	 * 自己实现数据为空的判断
	 * @return 数据是否为真的空
	 */
	boolean isResultDataEmpty();
}
