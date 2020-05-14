import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.googlecode.lanterna.terminal.Wincon;
import com.googlecode.lanterna.terminal.WindowsConsoleInputStream;
import com.googlecode.lanterna.terminal.WindowsConsoleOutputStream;

public class REPL {

	public static void main(String[] args) throws Exception {
		Charset charset = StandardCharsets.UTF_8;
		WindowsConsoleInputStream in = new WindowsConsoleInputStream(Wincon.INSTANCE.GetStdHandle(Wincon.STD_INPUT_HANDLE), charset);
		WindowsConsoleOutputStream out = new WindowsConsoleOutputStream(Wincon.INSTANCE.GetStdHandle(Wincon.STD_OUTPUT_HANDLE), charset);

		in.onKeyEvent(System.err::println);
		in.onWindowBufferSizeEvent(System.err::println);
		in.onMouseEvent(System.err::println);

		InputStreamReader reader = new InputStreamReader(in, charset);
		PrintStream printer = new PrintStream(out, true, charset);

		int i = 0;
		while ((i = reader.read()) != 27) {
			printer.format("%c%n", i);
		}

		System.exit(0);
	}

}
