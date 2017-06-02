package nl.fxtooly;

public class ToolyExceptionHandler {

	public static void handle(String message, Exception e){
		e.printStackTrace();
	}
	public static void handle(Exception e){
		e.printStackTrace();
	}

}
