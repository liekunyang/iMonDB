package inspector.imondb.collector.view.gui.overview;

/*
 * #%L
 * iMonDB Collector
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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import inspector.imondb.collector.model.InstrumentMap;
import inspector.imondb.collector.view.gui.CollectorFrame;
import inspector.imondb.collector.view.gui.database.DatabasePanel;
import inspector.imondb.collector.view.gui.general.GeneralPanel;
import inspector.imondb.collector.view.gui.instrument.InstrumentOverviewPanel;
import inspector.imondb.collector.view.gui.instrument.InstrumentsPanel;
import inspector.imondb.collector.view.gui.metadata.MetadataPanel;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Observable;

public class OverviewPanel extends Observable {

    private static ImageIcon iconError = new ImageIcon(OverviewPanel.class.getResource("/images/nok.png"));
    private static ImageIcon iconWarning = new ImageIcon(OverviewPanel.class.getResource("/images/warning.png"));
    private static ImageIcon iconValid = new ImageIcon(OverviewPanel.class.getResource("/images/ok.png"));

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new FormLayout("fill:d:noGrow,left:4dlu:noGrow,fill:max(d;4px):noGrow", "center:d:noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow,top:3dlu:noGrow,center:max(d;4px):noGrow"));
        panel.add(panel1);
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10), null));
        final JLabel label1 = new JLabel();
        label1.setHorizontalAlignment(11);
        label1.setText("<html><b>Database configuration:</b></html>");
        CellConstraints cc = new CellConstraints();
        panel1.add(label1, cc.xy(1, 1));
        final JLabel label2 = new JLabel();
        label2.setHorizontalAlignment(11);
        label2.setText("<html><b>General configuration:</b></html>");
        panel1.add(label2, cc.xy(1, 3));
        final JLabel label3 = new JLabel();
        label3.setHorizontalAlignment(11);
        label3.setText("<html><b>Instrument configuration:</b></html>");
        panel1.add(label3, cc.xy(1, 5));
        final JLabel label4 = new JLabel();
        label4.setHorizontalAlignment(11);
        label4.setText("Metadata configuration:");
        panel1.add(label4, cc.xy(1, 7));
        labelDatabase = new JLabel();
        labelDatabase.setIcon(new ImageIcon(getClass().getResource("/images/warning.png")));
        labelDatabase.setText("");
        panel1.add(labelDatabase, cc.xy(3, 1));
        labelGeneral = new JLabel();
        labelGeneral.setIcon(new ImageIcon(getClass().getResource("/images/warning.png")));
        labelGeneral.setText("");
        panel1.add(labelGeneral, cc.xy(3, 3));
        labelInstrument = new JLabel();
        labelInstrument.setIcon(new ImageIcon(getClass().getResource("/images/warning.png")));
        labelInstrument.setText("");
        panel1.add(labelInstrument, cc.xy(3, 5));
        labelMetadata = new JLabel();
        labelMetadata.setIcon(new ImageIcon(getClass().getResource("/images/warning.png")));
        labelMetadata.setText("");
        panel1.add(labelMetadata, cc.xy(3, 7));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

    public enum Status {
        VALID, WARNING, ERROR
    }

    private JPanel panel;

    private JLabel labelDatabase;
    private JLabel labelInstrument;
    private JLabel labelGeneral;
    private JLabel labelMetadata;

    private CollectorFrame collector;

    private Status globalStatus;

    public OverviewPanel(CollectorFrame collectorFrame) {
        this.collector = collectorFrame;
        this.globalStatus = Status.VALID;
    }

    public JPanel getPanel() {
        return panel;
    }

    public void update() {
        globalStatus = Status.VALID;

        // database configuration
        DatabasePanel databasePanel = collector.getDatabasePanel();
        switch(databasePanel.getConnectionStatus()) {
            case CONNECTED:
                setDatabaseStatus(Status.VALID, "Valid database configuration to " +
                        databasePanel.getUserName() + "@" + databasePanel.getHost() + ":" + databasePanel.getPort() +
                        "/" + databasePanel.getDatabase());
                break;
            case FAILED_CONNECTION:
                setDatabaseStatus(Status.ERROR, "Invalid database configuration");
                break;
            case IN_PROGRESS:
                setDatabaseStatus(Status.WARNING, "Unverified database configuration");
                break;
            case UNKNOWN:
                if(StringUtils.isEmpty(databasePanel.getHost()) || StringUtils.isEmpty(databasePanel.getPort()) ||
                        StringUtils.isEmpty(databasePanel.getDatabase()) || StringUtils.isEmpty(databasePanel.getUserName())) {
                    setDatabaseStatus(Status.ERROR, "Invalid database configuration");
                } else {
                    setDatabaseStatus(Status.WARNING, "Unverified database configuration");
                }
                break;
            default:
                break;
        }

        // general configuration
        GeneralPanel generalPanel = collector.getGeneralPanel();
        if(generalPanel.getDirectory() == null) {
            setGeneralStatus(Status.ERROR, "No start directory set");
        } else if(StringUtils.isEmpty(generalPanel.getFileNameRegex())) {
            setGeneralStatus(Status.ERROR, "No raw file name regex set");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("<html>Retrieving raw files matching \"").append(generalPanel.getFileNameRegex()).append("\"<br>")
                    .append("from \"").append(generalPanel.shortenPath(generalPanel.getDirectory()));
            if(generalPanel.getStartDate() != null) {
                sb.append("<br>modified after ").append((new SimpleDateFormat("dd/MM/yyyy")).format(generalPanel.getStartDate()));
            }
            sb.append("</html>");
            setGeneralStatus(Status.VALID, sb.toString());
        }

        // instrument configuration
        InstrumentsPanel instrumentsPanel = collector.getInstrumentsPanel();
        Collection<InstrumentMap> instrumentMaps = instrumentsPanel.getInstruments();
        if(instrumentMaps.isEmpty()) {
            setInstrumentStatus(Status.ERROR, "No instruments configured");
        } else {
            InstrumentOverviewPanel.InstrumentStatus status = InstrumentOverviewPanel.InstrumentStatus.INVALID;
            for(InstrumentMap instrumentMap : instrumentMaps) {
                InstrumentOverviewPanel.InstrumentStatus thisStatus = instrumentsPanel.getInstrumentStatus(instrumentMap);
                status = status.compareTo(thisStatus) < 0 ? status : thisStatus;
            }
            switch(status) {
                case VALID:
                    setInstrumentStatus(Status.VALID, "One or more valid instrument configurations");
                    break;
                case INVALID:
                    setInstrumentStatus(Status.ERROR, "One or more invalid instrument configurations");
                    break;
                case UNKNOWN:
                case NEW:
                    setInstrumentStatus(Status.WARNING, "One or more unverified/new instrument configurations");
                    break;
                default:
                    break;
            }
        }

        // metadata configuration
        MetadataPanel metadataPanel = collector.getMetadataPanel();
        if(metadataPanel.getMetadata().isEmpty()) {
            setMetadataStatus(Status.WARNING, "No metadata configurations set");
        } else {
            setMetadataStatus(Status.VALID, "One or more valid metadata configurations");
        }

        setChanged();
        notifyObservers(globalStatus);
    }

    private void setDatabaseStatus(Status status, String message) {
        setIcon(labelDatabase, status);
        labelDatabase.setToolTipText(message);

        globalStatus = globalStatus.compareTo(status) < 0 ? status : globalStatus;
    }

    private void setGeneralStatus(Status status, String message) {
        setIcon(labelGeneral, status);
        labelGeneral.setToolTipText(message);

        globalStatus = globalStatus.compareTo(status) < 0 ? status : globalStatus;
    }

    private void setInstrumentStatus(Status status, String message) {
        setIcon(labelInstrument, status);
        labelInstrument.setToolTipText(message);

        globalStatus = globalStatus.compareTo(status) < 0 ? status : globalStatus;
    }

    private void setMetadataStatus(Status status, String message) {
        setIcon(labelMetadata, status);
        labelMetadata.setToolTipText(message);

        globalStatus = globalStatus.compareTo(status) < 0 ? status : globalStatus;
    }

    private void setIcon(JLabel label, Status status) {
        switch(status) {
            case VALID:
                label.setIcon(iconValid);
                break;
            case ERROR:
                label.setIcon(iconError);
                break;
            case WARNING:
            default:
                label.setIcon(iconWarning);
                break;
        }
    }
}
