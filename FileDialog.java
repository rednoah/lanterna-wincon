import java.io.*;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.*;
import com.googlecode.lanterna.screen.*;
import com.googlecode.lanterna.terminal.*;

public class FileDialog {

	public static void main(String[] args) throws IOException {
		Terminal terminal = new DefaultTerminalFactory().createTerminal();
		Screen screen = new TerminalScreen(terminal);
		screen.startScreen();

		WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
		File selection = new FileDialogBuilder().build().showDialog(textGUI);

		screen.stopScreen();

		System.out.println(selection);
		System.exit(0);
	}

}
