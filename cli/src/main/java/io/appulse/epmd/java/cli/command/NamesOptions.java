package io.appulse.epmd.java.cli.command;

import io.appulse.epmd.java.cli.CommonOptions;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

@Slf4j
@Command(name = "names")
public class NamesOptions implements Runnable {

  @ParentCommand
  CommonOptions parent;

  @Override
  public void run () {
    log.info("NAMES");
  }
}
