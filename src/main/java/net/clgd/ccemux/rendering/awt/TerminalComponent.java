package net.clgd.ccemux.rendering.awt;

import dan200.computercraft.ComputerCraft;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.core.terminal.TextBuffer;
import net.clgd.ccemux.Utils;
import net.clgd.ccemux.emulation.CCEmuX;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

class TerminalComponent extends Canvas {
	private static final long serialVersionUID = 669029640511347733L;

	public static final String CC_FONT_PATH = "/assets/computercraft/textures/gui/termFont.png";

	private static Point getCharLocation(char c, int charWidth, int charHeight, int fontWidth, int fontHeight) {
		int columns = fontWidth / charWidth;
		int rows = fontHeight / charHeight;
		int charCode = (int) c;

		return new Point((charCode % columns) * charWidth, (charCode / rows) * charHeight);
	}

	public final Terminal terminal;
	public final int pixelWidth;
	public final int pixelHeight;
	public final int margin;

	public char cursorChar = '_';

	public boolean blinkLocked = false;

	BufferedImage[] fontImages;

	public TerminalComponent(Terminal terminal, double termScale) {
		this.pixelWidth = (int) (6 * termScale);
		this.pixelHeight = (int) (9 * termScale);
		this.margin = (int) (2 * termScale);
		this.terminal = terminal;

		fontImages = new BufferedImage[16];

		BufferedImage baseImage;
		try {
			baseImage = ImageIO.read(ComputerCraft.class.getResource(CC_FONT_PATH));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load CC font", e);
		}

		for (int i = 0; i < fontImages.length; i++) {
			fontImages[i] = Utils.makeTintedCopy(baseImage, Utils.getCCColourFromInt(i));
		}

		resizeTerminal(terminal.getWidth(), terminal.getHeight());
	}

	public void resizeTerminal(int width, int height) {
		Dimension termDimensions = new Dimension(width * pixelWidth + margin * 2, height * pixelHeight + margin * 2);

		setSize(termDimensions);
		setPreferredSize(termDimensions);
	}

	private void drawChar(Graphics g, char c, int x, int y, int color) {
		if ((int) c == 0)
			return; // nothing to do here

		// TODO: replace with something non-magical.
		int charWidth = 6;
		int charHeight = 9;

		int fontWidth = 96;
		int fontHeight = 144;

		Point charLocation = getCharLocation(c, charWidth, charHeight, fontWidth, fontHeight);

		g.drawImage(
				// tinted char
				fontImages[color],

				// destination
				x, y, x + pixelWidth, y + pixelHeight,

				// source
				charLocation.x, charLocation.y, charLocation.x + charWidth, charLocation.y + charHeight,

				null);
	}

	private void renderTerminal(double dt) {
		synchronized (terminal) {
			Graphics g = getBufferStrategy().getDrawGraphics();

			int dx = 0;
			int dy = 0;

			for (int y = 0; y < terminal.getHeight(); y++) {
				TextBuffer textLine = terminal.getLine(y);
				TextBuffer bgLine = terminal.getBackgroundColourLine(y);
				TextBuffer fgLine = terminal.getTextColourLine(y);

				int height = (y == 0 || y == terminal.getHeight() - 1) ? pixelHeight + margin : pixelHeight;

				for (int x = 0; x < terminal.getWidth(); x++) {
					int width = (x == 0 || x == terminal.getWidth() - 1) ? pixelWidth + margin : pixelWidth;

					g.setColor(Utils.getCCColourFromChar((bgLine == null) ? 'f' : bgLine.charAt(x)));
					g.fillRect(dx, dy, width, height);

					char character = (textLine == null) ? ' ' : textLine.charAt(x);
					char fgChar = (fgLine == null) ? ' ' : fgLine.charAt(x);

					drawChar(g, character, x * pixelWidth + margin, y * pixelHeight + margin,
							Utils.base16ToInt(fgChar));

					dx += width;
				}

				dx = 0;
				dy += height;
			}

			boolean blink = terminal.getCursorBlink() && (blinkLocked || CCEmuX.getGlobalCursorBlink());

			if (blink) {
				drawChar(g, cursorChar, terminal.getCursorX() * pixelWidth + margin,
						terminal.getCursorY() * pixelHeight + margin, terminal.getTextColour());
			}

			g.dispose();
		}
	}

	public void render(double dt) {
		if (getBufferStrategy() == null) {
			createBufferStrategy(2);
		}

		do {
			do {
				renderTerminal(dt);
			} while (getBufferStrategy().contentsRestored());

			getBufferStrategy().show();
		} while (getBufferStrategy().contentsLost());
	}
}
