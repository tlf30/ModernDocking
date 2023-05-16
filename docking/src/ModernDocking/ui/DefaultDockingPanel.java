/*
Copyright (c) 2023 Andrew Auclair

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package ModernDocking.ui;

import ModernDocking.Dockable;
import ModernDocking.DockableStyle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DefaultDockingPanel implements Dockable {
    private String persistentID;
    private int type;
    private String tabText;
    private Icon icon;
    private boolean floatingAllowed;
    private boolean limitToRoot;
    private DockableStyle style;
    private boolean canBeClosed;
    private boolean allowPinning;
    private boolean allowMinMax;
    private List<JMenu> moreOptions = new ArrayList<>();

    @Override
    public String getPersistentID() {
        return persistentID;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getTabText() {
        return tabText;
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public boolean isFloatingAllowed() {
        return floatingAllowed;
    }

    @Override
    public boolean shouldLimitToRoot() {
        return limitToRoot;
    }

    @Override
    public DockableStyle getStyle() {
        return style;
    }

    @Override
    public boolean canBeClosed() {
        return canBeClosed;
    }

    @Override
    public boolean allowPinning() {
        return allowPinning;
    }

    @Override
    public boolean allowMinMax() {
        return allowMinMax;
    }

    @Override
    public boolean hasMoreOptions() {
        return moreOptions.size() > 0;
    }

    @Override
    public void addMoreOptions(JPopupMenu menu) {
        for (JMenu option : moreOptions) {
            menu.add(option);
        }
    }
}