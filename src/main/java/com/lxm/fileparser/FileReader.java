package com.lxm.fileparser;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.lxm.fileparser.decoder.Decoder;

/**
 * 
 * ClassName: com.lxm.fileparser.FileReader <br/>
 * Function: 文件读取器 <br/>
 * Date: 2017年7月31日 <br/>
 * @author liuxiangming
 */
public class FileReader<T> implements Iterable<List<T>> {
	
	/**
	 * 解码器
	 */
	private Decoder<T> decoder;
	
	/**
	 * 文件迭代器
	 */
	private Iterator<File> fileIterator;
	
	/**
	 * 一次读取块大小
	 */
	private long chunkSize = 4096;
	
	public FileReader(Decoder<T> decoder, File ...files) {
		this(decoder, Arrays.asList(files));
	}
	
	public FileReader(Decoder<T> decoder, List<File> fileList) {
		this.decoder = decoder;
		this.fileIterator = fileList.iterator();
	}

	@Override
	public Iterator<List<T>> iterator() {
		// TODO Auto-generated method stub
		return new Iterator<List<T>>() {
			/**
			 * 取块的位置
			 */
			private long chunkPos = 0;
			
			/**
			 * 文件映射块buffer
			 */
			private MappedByteBuffer byteBuffer;
			
			/**
			 * 文件通道
			 */
			private FileChannel fileChannel;
			
			/**
			 * 解码后结果列表
			 */
			private List<T> results;

			@Override
			public boolean hasNext() {
				if(byteBuffer == null || !byteBuffer.hasRemaining())/* 取出要解析的byteBuffer */{
					try {
						byteBuffer = nextBuffer(chunkPos);
						if(byteBuffer == null){
							return false;
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				T result = null;
				while ((result = decoder.decode(byteBuffer)) != null)/* 对byteBuffer进行解析，并保存解析结果 */ {
					if(results == null){
						results = new ArrayList<>();
					}
					results.add(result);
				}
				chunkPos += byteBuffer.position(); //记录fileChannel中下次映射开始位置
				byteBuffer = null;
				if(results != null){
					return true;
				}else/* results为null,说明此时的fileChannel取不到可解析的字节块，可关闭 */{
					if(fileChannel != null){
						try {
							fileChannel.close();
							fileChannel = null;
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
				return false;
			}

			@Override
			public List<T> next() {
				List<T> res = results;
				results = null;
				return res;
			}
			
			/**
			 * 取下一个块buffer
			 * @param position fileChannel映射byteBuffer的开始位置
			 * @return
			 * @throws IOException
			 */
			@SuppressWarnings("resource")
			private MappedByteBuffer nextBuffer(long position) throws IOException{
				try {
					if(fileChannel == null || fileChannel.size()== position){
						if(fileChannel != null){
							fileChannel.close();
							fileChannel = null;
						}
						if(fileIterator.hasNext()){
							File file = fileIterator.next();
							fileChannel = new RandomAccessFile(file, "r").getChannel();
							chunkPos = 0;
							position = 0;
						}else{
							return null;
						}
					}
					long size = chunkSize;
					if(fileChannel.size() - position < size){
						size = fileChannel.size() - position;
					}
					return fileChannel.map(FileChannel.MapMode.READ_ONLY, position, size);
				} catch (IOException e) {
					if(fileChannel != null){
						fileChannel.close();
						fileChannel = null;
					}
					throw e;
				}
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}
		};
	}

}
