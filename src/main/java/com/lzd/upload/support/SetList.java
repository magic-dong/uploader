package com.lzd.upload.support;

import java.util.LinkedList;

/**
 * 自定义集合有序且元素不重复的集合
 * @author lzd
 * @date 2019年7月26日
 * @version 
 * @param <E>
 */
public class SetList<E> extends LinkedList<E>{
	
	private static final long serialVersionUID = 3612971767507405567L;
	
	@Override
	public boolean add(E e) {
		// TODO Auto-generated method stub
		if (size() == 0) {
			return super.add(e);
		} else {
			int count = 0;
			for (E t : this) {
				if (t.equals(e)) {
					count++;
					break;
				}
			}
			return count == 0 ? super.add(e):false;
		}
	}
}
