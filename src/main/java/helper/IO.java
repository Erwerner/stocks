package helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class IO {
	public static Object getEnumFromInput(String message, Object[] values) {
		while (true) {
			System.out.println(message);
			String input;
			for (Object value : values)
				System.out.println("[" + value + "]");

			try {
				System.out.print(">");
				input = new BufferedReader(new InputStreamReader(System.in))
						.readLine();
				for (Object value : values)
					if (value.toString().equals(input))
						return value;
				System.out.println("Invalid!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
