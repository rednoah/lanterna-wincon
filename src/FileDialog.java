import java.io.File;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.gui2.dialogs.FileDialogBuilder;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

public class FileDialog {

	public static void main(String[] args) throws Exception {
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
