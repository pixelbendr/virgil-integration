package com.psyphertxt.android.cyfa.ui.commands;

import android.view.View;

/**
 * Interface for implementing actionable UI commands
 *
 */
public interface Command {
    void execute(final View view, CommandListener listener);
}