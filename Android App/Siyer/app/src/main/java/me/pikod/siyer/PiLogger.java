package me.pikod.siyer;

public class PiLogger {

    public void Info(String log){
        Log("INFO", log);
    }

    public void Warn(String log){
        Log("WARN", log);
    }

    private void Log(String level, String log){
        System.out.println("["+level+"]: "+log);
    }

}
