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
package com.l2jfree.network.mmocore;

import java.nio.ByteOrder;

import com.l2jfree.util.Introspection;

/**
 * This class provides a convenient way to pass specific parameters to different parts of MMOCore as
 * well as to document these parameters. Once the {@link MMOController} has been initialized with a
 * {@link MMOConfig} instance, it has no further use, and simply dropped.
 * 
 * @author KenM (reference)
 * @author NB4L1 (l2jfree)
 * @author savormix (l2jfree)
 */
public final class MMOConfig
{
	/**
	 * Specifies the maximum packet size for a network protocol that uses a word to declare the
	 * packet's size.
	 */
	public static final int DEFAULT_MAX_PACKET_SIZE = 64 * 1024 - 1;
	
	private final String _name;
	private boolean _modifiable;
	
	private final int _minBufferSize;
	private int _bufferSize;
	
	private int _maxOutgoingPacketsPerPass;
	private int _maxIncomingPacketsPerPass;
	
	private int _maxOutgoingBytesPerPass;
	private int _maxIncomingBytesPerPass;
	
	private long _selectorSleepTime;
	
	private int _helperBufferCount;
	
	private ByteOrder _byteOrder;
	
	private int _threadCount;
	
	/**
	 * Creates a MMOCore configuration to be passed to a
	 * {@link com.l2jfree.network.mmocore.MMOController}. <BR>
	 * <BR>
	 * This configuration automatically picks an optimal buffer size based on a rule of thumb that
	 * this value should be equal to:<BR>
	 * <I>Maximum length of a valid packet + 1</I><BR>
	 * When a largest possible packet is read, the last byte helps to identify whether there
	 * <U>may</U> be available bytes in the channel/socket. <BR>
	 * <BR>
	 * Setting a higher buffer size via {@link #setBufferSize(int)} may reduce network I/O load as
	 * less socket write/read calls will be done. However, this is only useful in situations where
	 * large amounts of [large] packets are being transfered AND the network throughput is high
	 * (like <A href="http://www.infinibandta.org/">InfiniBand</A>).
	 * 
	 * @param name name of configuration
	 * @param maxPacketSize maximum allowed packet size
	 */
	public MMOConfig(String name, int maxPacketSize)
	{
		if (maxPacketSize < 1)
			throw new IllegalArgumentException(
					"To ensure data flow, the maximum allowed packet size must be at least 1 byte.");
		
		_name = name;
		_modifiable = true;
		_minBufferSize = maxPacketSize;
		_bufferSize = (maxPacketSize == Integer.MAX_VALUE ? Integer.MAX_VALUE : ++maxPacketSize);
		_maxOutgoingPacketsPerPass = Integer.MAX_VALUE;
		_maxIncomingPacketsPerPass = Integer.MAX_VALUE;
		_maxOutgoingBytesPerPass = Integer.MAX_VALUE;
		_maxIncomingBytesPerPass = Integer.MAX_VALUE;
		_selectorSleepTime = 10;
		_helperBufferCount = 20;
		_byteOrder = ByteOrder.LITTLE_ENDIAN;
		_threadCount = Runtime.getRuntime().availableProcessors();
	}
	
	/**
	 * Creates a MMOCore configuration to be passed to a
	 * {@link com.l2jfree.network.mmocore.MMOController}. <BR>
	 * <BR>
	 * It is assumed that the maximum packet size is {@value #DEFAULT_MAX_PACKET_SIZE}.
	 * 
	 * @param name name of configuration
	 * @see #MMOConfig(String, int)
	 */
	public MMOConfig(String name)
	{
		this(name, DEFAULT_MAX_PACKET_SIZE);
	}
	
	/**
	 * Returns the name of this configuration.
	 * 
	 * @return name of configuration
	 */
	public String getName()
	{
		return _name;
	}
	
	private void tryModify() throws IllegalStateException
	{
		if (!isModifiable())
			throw new IllegalStateException("Configuration already in use.");
	}
	
	/**
	 * Allows or prevents modification of this configuration.
	 * 
	 * @param modifiable whether to allow modification
	 */
	void setModifiable(boolean modifiable)
	{
		tryModify();
		_modifiable = modifiable;
	}
	
	/**
	 * Returns whether this configuration can be altered. <BR>
	 * <BR>
	 * Configurations that have already been used cannot be altered.
	 * 
	 * @return is this configuration modifiable
	 */
	public boolean isModifiable()
	{
		return _modifiable;
	}
	
	/**
	 * Returns the minimum allowed size (in bytes) of byte buffers used in network I/O. <BR>
	 * <BR>
	 * Defaults to the specified maximum packet size.
	 * 
	 * @return buffer's size in bytes
	 */
	public int getMinBufferSize()
	{
		return _minBufferSize;
	}
	
	/**
	 * Sets the size (in bytes) of byte buffers used in network I/O. <BR>
	 * <BR>
	 * Defaults to {@link #getMinBufferSize()} + 1.
	 * 
	 * @param bufferSize buffer's size in bytes
	 * @throws IllegalArgumentException if <TT>bufferSize</TT> < {@link #getMinBufferSize()}
	 * @throws IllegalStateException if this configuration is already in use
	 */
	public void setBufferSize(int bufferSize) throws IllegalArgumentException, IllegalStateException
	{
		tryModify();
		
		if (bufferSize < getMinBufferSize())
			throw new IllegalArgumentException("Buffer's size is too low.");
		
		_bufferSize = bufferSize;
	}
	
	/**
	 * Returns the desired size (in bytes) of byte buffers used in network I/O. <BR>
	 * <BR>
	 * Defaults to {@link #getMinBufferSize()} + 1.
	 * 
	 * @return buffer's size in bytes
	 */
	public int getBufferSize()
	{
		return _bufferSize;
	}
	
	/**
	 * Sets the amount of "helper" byte buffers kept in cache for further usage. <BR>
	 * <BR>
	 * Defaults to 20 byte buffers.
	 * 
	 * @param helperBufferCount count of additional byte buffers
	 * @throws IllegalArgumentException if <TT>helperBufferCount</TT> < 0
	 * @throws IllegalStateException if this configuration is already in use
	 */
	public void setHelperBufferCount(int helperBufferCount) throws IllegalStateException
	{
		tryModify();
		
		if (helperBufferCount < 0)
			throw new IllegalArgumentException("Invalid helper buffer count.");
		
		_helperBufferCount = helperBufferCount;
	}
	
	/**
	 * Returns the desired amount of "helper" byte buffers kept in cache for further usage. <BR>
	 * <BR>
	 * Defaults to 20 byte buffers.
	 * 
	 * @return count of additional byte buffers
	 */
	public int getHelperBufferCount()
	{
		return _helperBufferCount;
	}
	
	/**
	 * Sets the byte order of byte buffers used in network I/O. <BR>
	 * <BR>
	 * Defaults to {@link java.nio.ByteOrder#LITTLE_ENDIAN}.
	 * 
	 * @param byteOrder {@link java.nio.ByteOrder#BIG_ENDIAN} or
	 *            {@link java.nio.ByteOrder#LITTLE_ENDIAN}
	 * @throws IllegalStateException if this configuration is already in use
	 * @see java.nio.ByteOrder#nativeOrder()
	 */
	public void setByteOrder(ByteOrder byteOrder) throws IllegalStateException
	{
		tryModify();
		_byteOrder = byteOrder;
	}
	
	/**
	 * Returns the desired byte order of byte buffers used in network I/O. <BR>
	 * <BR>
	 * Defaults to {@link java.nio.ByteOrder#LITTLE_ENDIAN}.
	 * 
	 * @return buffer's byte order
	 */
	public ByteOrder getByteOrder()
	{
		return _byteOrder;
	}
	
	/**
	 * Instructs server to send at most {@code maxOutgoingPacketsPerPass} packets in a single socket
	 * write call. <BR>
	 * <BR>
	 * Less packets may be sent if the connection drops, the underlying channel's send buffer is
	 * completely filled or the number of outgoing bytes reaches the configured limit. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @param maxOutgoingPacketsPerPass maximum number of packets to be sent at once
	 * @throws IllegalArgumentException if <TT>maxOutgoingPacketsPerPass</TT> < 1
	 * @throws IllegalStateException if this configuration is already in use
	 * @see #setMaxOutgoingBytesPerPass(int)
	 */
	public void setMaxOutgoingPacketsPerPass(int maxOutgoingPacketsPerPass) throws IllegalArgumentException,
			IllegalStateException
	{
		tryModify();
		
		if (maxOutgoingPacketsPerPass < 1)
			throw new IllegalArgumentException("At least one packet must be sent per pass");
		
		_maxOutgoingPacketsPerPass = maxOutgoingPacketsPerPass;
	}
	
	/**
	 * Returns the desired maximum amount of packets to be sent in a single socket write call. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @return maximum number of packets to be sent at once
	 * @see #getMaxOutgoingBytesPerPass()
	 */
	public int getMaxOutgoingPacketsPerPass()
	{
		return _maxOutgoingPacketsPerPass;
	}
	
	/**
	 * Instructs server to read at most {@code maxIncomingPacketsPerPass} packets in a single socket
	 * read call. <BR>
	 * <BR>
	 * Less packets may be read if the connection drops, the underlying channel's read buffer is
	 * completely exhausted or the number of incoming bytes reaches the configured limit. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @param maxIncomingPacketsPerPass maximum number of packets to read at once
	 * @throws IllegalArgumentException if <TT>maxIncomingPacketsPerPass</TT> < 1
	 * @throws IllegalStateException if this configuration is already in use
	 * @see #setMaxIncomingBytesPerPass(int)
	 */
	public void setMaxIncomingPacketsPerPass(int maxIncomingPacketsPerPass) throws IllegalArgumentException,
			IllegalStateException
	{
		tryModify();
		
		if (maxIncomingPacketsPerPass < 1)
			throw new IllegalArgumentException("At least one packet must be read per pass");
		
		_maxIncomingPacketsPerPass = maxIncomingPacketsPerPass;
	}
	
	/**
	 * Returns the desired maximum amount of packets to be read in a single socket read call. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @return maximum number of packets to read at once
	 * @see #getMaxIncomingBytesPerPass()
	 */
	public int getMaxIncomingPacketsPerPass()
	{
		return _maxIncomingPacketsPerPass;
	}
	
	/**
	 * Instructs server to send at most {@code maxOutgoingBytesPerPass} bytes in a single socket
	 * write call. <BR>
	 * <BR>
	 * Less bytes may be sent if the connection drops, the underlying channel's send buffer is
	 * completely filled or the number of outgoing packets reaches the configured limit. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @param maxOutgoingBytesPerPass maximum number of bytes to be sent at once
	 * @throws IllegalArgumentException if <TT>maxOutgoingBytesPerPass</TT> < 1
	 * @throws IllegalStateException if this configuration is already in use
	 * @see #setMaxOutgoingPacketsPerPass(int)
	 */
	public void setMaxOutgoingBytesPerPass(int maxOutgoingBytesPerPass) throws IllegalArgumentException,
			IllegalStateException
	{
		tryModify();
		
		if (maxOutgoingBytesPerPass < 1)
			throw new IllegalArgumentException("At least one byte must be sent per pass.");
		
		_maxOutgoingBytesPerPass = maxOutgoingBytesPerPass;
	}
	
	/**
	 * Returns the desired maximum amount of bytes to be sent in a single socket write call. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @return maximum number of bytes to be sent at once
	 * @see #getMaxOutgoingPacketsPerPass()
	 */
	public int getMaxOutgoingBytesPerPass()
	{
		return _maxOutgoingBytesPerPass;
	}
	
	/**
	 * Instructs server to read at most {@code maxIncomingBytesPerPass} bytes in a single socket
	 * read call. <BR>
	 * <BR>
	 * Less bytes may be read if the connection drops, the underlying channel's read buffer is
	 * completely exhausted or the number of incoming packets reaches the configured limit. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @param maxIncomingBytesPerPass maximum number of bytes to be sent at once
	 * @throws IllegalArgumentException if <TT>maxIncomingBytesPerPass</TT> < 1
	 * @throws IllegalStateException if this configuration is already in use
	 * @see #setMaxIncomingPacketsPerPass(int)
	 */
	public void setMaxIncomingBytesPerPass(int maxIncomingBytesPerPass) throws IllegalArgumentException,
			IllegalStateException
	{
		tryModify();
		
		if (maxIncomingBytesPerPass < 1)
			throw new IllegalArgumentException("At least one byte must be read per pass.");
		
		_maxIncomingBytesPerPass = maxIncomingBytesPerPass;
	}
	
	/**
	 * Returns the desired maximum amount of bytes to be read in a single socket read call. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Integer#MAX_VALUE}.
	 * 
	 * @return maximum number of bytes to be read at once
	 * @see #getMaxIncomingPacketsPerPass()
	 */
	public int getMaxIncomingBytesPerPass()
	{
		return _maxIncomingBytesPerPass;
	}
	
	/**
	 * Instructs the selector thread to sleep for {@code selectorSleepTime} milliseconds between
	 * iterations.<BR>
	 * Lower values decrease latency, higher values increase throughput. <BR>
	 * <BR>
	 * Extremely low values (<= 1 ms) will provide nearly no latency at the cost of wasting CPU time
	 * due to very frequent network I/O.<BR>
	 * High values (> 100 ms) tend to give noticeable latency and CPU usage spikes due to longer I/O
	 * coupled with longer idle times.
	 * <UL>
	 * <LI>5 or less for a [pseudo] real-time service (Geo/PF/Login Server <-> Game Server)</LI>
	 * <LI>5-15 for any interactive service (Game Server <-> Client)</LI>
	 * <LI>25-50 (or possibly higher) for an authorization service (Login Server <-> Client)</LI>
	 * </UL>
	 * 
	 * @param selectorSleepTime selector wakeup interval in milliseconds
	 * @throws IllegalArgumentException if <TT>selectorSleepTime</TT> < 1
	 * @throws IllegalStateException if this configuration is already in use
	 */
	public void setSelectorSleepTime(long selectorSleepTime) throws IllegalArgumentException, IllegalStateException
	{
		tryModify();
		
		if (selectorSleepTime < 1)
			throw new IllegalArgumentException("Invalid sleep time.");
		
		_selectorSleepTime = selectorSleepTime;
	}
	
	/**
	 * Returns the desired selector thread's sleep (idling) time between iterations. <BR>
	 * <BR>
	 * Defaults to 10 milliseconds.
	 * 
	 * @return selector wakeup interval in milliseconds
	 */
	public long getSelectorSleepTime()
	{
		return _selectorSleepTime;
	}
	
	/**
	 * Sets the amount of network I/O threads. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Runtime#availableProcessors()}.
	 * 
	 * @param threadCount network thread count
	 * @throws IllegalArgumentException if <TT>threadCount</TT> < 1
	 * @throws IllegalStateException if this configuration is already in use
	 */
	public void setThreadCount(int threadCount) throws IllegalArgumentException, IllegalStateException
	{
		tryModify();
		
		if (threadCount < 1)
			throw new IllegalArgumentException("At least one thread is required.");
		
		_threadCount = threadCount;
	}
	
	/**
	 * Returns the desired amount of network I/O threads. <BR>
	 * <BR>
	 * Defaults to {@link java.lang.Runtime#availableProcessors()}.
	 * 
	 * @return network thread count
	 */
	public int getThreadCount()
	{
		return _threadCount;
	}
	
	@Override
	public String toString()
	{
		return Introspection.toString(this);
	}
}
