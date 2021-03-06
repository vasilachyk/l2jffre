/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.l2jfree.tools.random;

import java.util.Random;

public final class Rnd
{
	private static final Random[] RANDOMS = new Random[1 << 4];
	
	static
	{
		for (int i = 0; i < RANDOMS.length; i++)
			RANDOMS[i] = new Random();
	}
	
	private static int randomizer;
	
	private static Random rnd()
	{
		return RANDOMS[randomizer++ & (RANDOMS.length - 1)];
	}
	
	/**
	 * Get random number from 0.0 to 1.0
	 */
	public static double nextDouble()
	{
		return rnd().nextDouble();
	}
	
	/**
	 * Get random number from 0 to n-1
	 */
	public static int nextInt(int n)
	{
		if (n < 0)
			return rnd().nextInt(-n) * -1;
		else if (n == 0)
			return 0;
		
		return rnd().nextInt(n);
	}
	
	/**
	 * Get random number from 0 to n-1
	 */
	public static int get(int n)
	{
		return nextInt(n);
	}
	
	/**
	 * Get random number from min to max <b>(not max-1)</b>
	 */
	public static int get(int min_, int max_)
	{
		int min = Math.min(min_, max_);
		int max = Math.max(min_, max_);
		
		return min + nextInt(max - min + 1);
	}
	
	public static double nextGaussian()
	{
		return rnd().nextGaussian();
	}
	
	public static boolean nextBoolean()
	{
		return rnd().nextBoolean();
	}
	
	public static byte[] nextBytes(byte[] array)
	{
		rnd().nextBytes(array);
		
		return array;
	}
}