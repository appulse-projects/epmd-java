package io.appulse.epmd.java.cli.command;

import io.appulse.epmd.java.cli.CommonOptions;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;

@Slf4j
@Command(name = "stop")
public class StopCommand implements Runnable {

  @ParentCommand
  CommonOptions parent;

  @Parameters(paramLabel = "NAME")
  String name;

  @Override
  public void run () {
    log.info("STOP {}", name);
  }
}
