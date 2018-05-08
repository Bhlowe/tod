/*
 * Created on Jul 26, 2006
 */
package zz.utils.bit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import zz.utils.Utils;


public class TestPerformance
{
	@Test public void compare()
	{
		final int theSize = 200*1000*1000;
		
		
//		testArray(theSize);
//		runTest("Array", new Runnable()
//		{
//			public void run()
//			{
//				testArray(theSize);
//			}
//		});
		
		testStream(theSize);
		runTest("Stream", new Runnable()
		{
			public void run()
			{
				testStream(theSize);
			}
		});
		
		testBitStruct(theSize);
		runTest("BitStruct", new Runnable()
		{
			public void run()
			{
				testBitStruct(theSize);
			}
		});
		
		testIntBitStruct(theSize);
		runTest("IntBitStruct", new Runnable()
		{
			public void run()
			{
				testIntBitStruct(theSize);
			}
		});
//		
//		testIntBitArray(theSize);
//		runTest("IntBitArray", new Runnable()
//		{
//			public void run()
//			{
//				testIntBitArray(theSize);
//			}
//		});
//		
	}
	
	private long runTest(String aName, Runnable aRunnable)
	{
		long t0 = System.currentTimeMillis();
		aRunnable.run();
		long t1 = System.currentTimeMillis();
		long dt = t1-t0;
		
		System.out.println(aName + ": "+dt+"ms");
		return dt;
	}
	
	private void testArray(int aSize)
	{ 
		int n = aSize;
		byte[] theArray = new byte[aSize];
		
		int k=12;
		int mask = BitUtils.pow2i(k)-1;
		
		int f = (n-1)/BitUtils.pow2i(k);
		
		System.out.println(n);
		for (int i = 0; i < n; i++)
		{
			int theIndex = ((i & mask) * f)+(i >> k);
			theArray[theIndex] = 0;
//			theArray[i] = 0;
		}
	}

	private void testStream(int aSize)
	{
		try
		{
			DataOutputStream theStream = new DataOutputStream(new ByteArrayOutputStream(aSize));
			while (aSize > 0)
			{
				theStream.writeLong(0);
				aSize -= 8;
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private void testBitStruct(int aSize)
	{
		int theSize = aSize * 8;
		ByteBitStruct theBitStruct = new ByteBitStruct(theSize);
		while (theSize >= 63)
		{
			theBitStruct.writeLong(0, 63);
			theSize -= 63;
		}
	}
	
	private void testIntBitStruct(int aSize)
	{
		int theSize = aSize * 8;
		IntBitStruct theBitStruct = new IntBitStruct(theSize);
		while (theSize >= 63)
		{
			theBitStruct.writeLong(0, 63);
			theSize -= 63;
		}
	}
	
	private void testIntBitArray(int aSize)
	{
		int theSize = aSize * 8;
		int[] theArray = new int[aSize/4];
		int thePos = 0;
		while (theSize >= 63)
		{
			BitUtils.writeLong(theArray, 0, 0, thePos, 63);
			theSize -= 63;
			thePos += 63;
		}
	}
	
	@Test public void testMemset()
	{
		final int[] theArray = new int[100*1000*1000];

		long theLoopTime = runTest("loop", new Runnable()
		{
			public void run()
			{
				Utils.memset(theArray, 0, theArray.length);
			}
		});

		
		for(int i=0;i<10;i++)
		{
			final int k = BitUtils.pow2i(i);
			long theMemsetTime = runTest("memset, k="+k, new Runnable()
			{
				public void run()
				{
					Utils.memset(theArray, 0, k);
				}
			});
			
			System.out.println("ratio: "+(1.0f * theMemsetTime / theLoopTime));
			
		}
	}
	
	public static void main(String[] args)
	{
		new TestPerformance().compare();
	}
	
}
