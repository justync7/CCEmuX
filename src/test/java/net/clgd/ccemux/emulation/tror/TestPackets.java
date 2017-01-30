package net.clgd.ccemux.emulation.tror;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class TestPackets {
	@Test
	public void testBackgroundColorPacket() {
		assertEquals("TK:;a\n", new BackgroundColorPacket('a').toString());
		assertEquals("TK:1;0\n", new BackgroundColorPacket('0').toString("1"));
	}
	
	@Test
	public void testBlitLinePacket() {
		assertEquals("TY:;aaaa,dddd,text\n", new BlitLinePacket("text", "aaaa", "dddd").toString());
	}
	
	@Test
	public void testClearLinePacket() {
		assertEquals("TL:;\n", new ClearLinePacket().toString());
	}
	
	@Test
	public void testClearPacket() {
		assertEquals("TE:;\n", new ClearPacket().toString());
	}
	
	@Test
	public void testCursorBlinkPacket() {
		assertEquals("TB:;false\n", new CursorBlinkPacket(false).toString());
	}
	
	@Test
	public void testCursorPosPacket() {
		assertEquals("TC:;3,5\n", new CursorPosPacket(3, 5).toString());
	}
	
	@Test
	public void testResizePacket() {
		assertEquals("TR:;51,19\n", new ResizePacket(51, 19).toString());
	}
	
	@Test
	public void testScrollPacket() {
		assertEquals("TS:;3\n", new ScrollPacket(3).toString());
	}
	
	@Test
	public void testTextColorPacket() {
		assertEquals("TF:;3\n", new TextColorPacket('3').toString());
	}
	
	@Test
	public void testWritePacket() {
		assertEquals("TW:;text\n", new WritePacket("text").toString());
	}
	
	@Test
	public void testCapabilitiesPacket() {
		assertEquals("SP:;-ext1-ext2-ext3-\n", new CapabilitiesPacket(ImmutableSet.of("ext1", "ext2", "ext3")).toString());
	}
	
	@Test
	public void testConnectionClosedPacket() {
		assertEquals("SC:;test\n", new ConnectionClosedPacket("test").toString());
	}
}