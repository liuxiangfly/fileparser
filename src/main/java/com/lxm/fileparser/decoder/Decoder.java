package com.lxm.fileparser.decoder;

import java.nio.ByteBuffer;

/**
 * 
 * ClassName: com.lxm.fileparser.decoder.Decoder <br/>
 * Function: 解析器 <br/>
 * Date: 2017年7月31日 <br/>
 * @author liuxiangming
 */
public interface Decoder<T> {
	
	/**
	 * 将byteBuffer中的字节解析成T
	 * @param byteBuffer
	 * @return
	 */
	public T decode(ByteBuffer byteBuffer);

}
