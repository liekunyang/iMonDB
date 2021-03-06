package inspector.imondb.viewer.view.gui;

/*
 * #%L
 * iMonDB Viewer
 * %%
 * Copyright (C) 2014 - 2015 InSPECtor
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.bric.swing.ColorPicker;
import com.bric.swing.ColorWell;
import inspector.imondb.model.EventType;
import inspector.imondb.viewer.model.VisualizationConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class VisualizationConfigPanel {

    private ViewerFrame viewerFrame;

    private JPanel panel;

    private ColorWell colorUndefined;
    private ColorWell colorCalibration;
    private ColorWell colorMaintenance;
    private ColorWell colorIncident;

    public VisualizationConfigPanel(ViewerFrame viewerFrame, VisualizationConfiguration configuration) {
        this.viewerFrame = viewerFrame;

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        JLabel title = new JLabel("Specify the color of the various event types.");
        panel.add(title);
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 20, 0));

        JPanel colorPanel = new JPanel(new SpringLayout());

        colorPanel.add(new JLabel("Undefined:", JLabel.TRAILING));
        colorUndefined = createColorWell(configuration.getColor(EventType.UNDEFINED));
        colorPanel.add(colorUndefined);

        colorPanel.add(new JLabel("Calibration:", JLabel.TRAILING));
        colorCalibration = createColorWell(configuration.getColor(EventType.CALIBRATION));
        colorPanel.add(colorCalibration);

        colorPanel.add(new JLabel("Maintenance:", JLabel.TRAILING));
        colorMaintenance = createColorWell(configuration.getColor(EventType.MAINTENANCE));
        colorPanel.add(colorMaintenance);

        colorPanel.add(new JLabel("Incident:", JLabel.TRAILING));
        colorIncident = createColorWell(configuration.getColor(EventType.INCIDENT));
        colorPanel.add(colorIncident);

        SpringUtilities.makeCompactGrid(colorPanel, 4, 2, 6, 6, 6, 6);

        panel.add(colorPanel);
    }

    public JPanel getPanel() {
        return panel;
    }

    public Color getColor(EventType type) {
        switch(type) {
            case UNDEFINED:
                return colorUndefined.getColor();
            case CALIBRATION:
                return colorCalibration.getColor();
            case MAINTENANCE:
                return colorMaintenance.getColor();
            case INCIDENT:
                return colorIncident.getColor();
            default:
                return Color.BLACK;
        }
    }

    private ColorWell createColorWell(Color color) {
        ColorWell colorWell = new ColorWell(color);
        // remove default mouse listener
        for(MouseListener listener : colorWell.getMouseListeners()) {
            colorWell.removeMouseListener(listener);
        }
        colorWell.addMouseListener(new ColorWellMouseListener());

        return colorWell;
    }

    private class ColorWellMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            ColorWell source = (ColorWell) e.getSource();
            Color color = ColorPicker.showDialog(viewerFrame.getFrame(), source.getColor());
            source.setColor(color != null ? color : source.getColor());
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // do nothing
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // do nothing
        }
    }
}

