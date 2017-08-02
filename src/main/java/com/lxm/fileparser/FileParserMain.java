package com.lxm.fileparser;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import com.lxm.fileparser.decoder.TextRowDecoder;

/**
 * 
 * ClassName: com.lxm.fileparser.FileParserMain <br/>
 * Function: 测试类 <br/>
 * Date: 2017年7月31日 <br/>
 * @author liuxiangming
 */
public class FileParserMain {

	public static void main(String[] args) {
		File file = new File("D:/data/javatest.txt");
		TextRowDecoder decoder = new TextRowDecoder(4, (byte) ',');
		FileReader<byte[][]> reader = new FileReader<>(decoder, file);
		Iterator<List<byte[][]>> it = reader.iterator();
		while (it.hasNext()) {
			List<byte[][]> rows = it.next();
			for (byte[][] row : rows) {
				System.out.println(new String(row[0]));
				System.out.println(new String(row[1]));
				System.out.println(new String(row[2]));
				System.out.println(new String(row[3]));
			}
		}

	}
}
