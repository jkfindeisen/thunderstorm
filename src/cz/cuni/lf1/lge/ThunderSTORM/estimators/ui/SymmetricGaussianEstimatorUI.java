package cz.cuni.lf1.lge.ThunderSTORM.estimators.ui;

import cz.cuni.lf1.lge.ThunderSTORM.estimators.LSQFitter;
import cz.cuni.lf1.lge.ThunderSTORM.estimators.IEstimator;
import cz.cuni.lf1.lge.ThunderSTORM.estimators.MLEFitter;
import cz.cuni.lf1.lge.ThunderSTORM.estimators.MultipleLocationsImageFitting;
import cz.cuni.lf1.lge.ThunderSTORM.estimators.PSF.SymmetricGaussianPSF;
import cz.cuni.lf1.lge.ThunderSTORM.util.GridBagHelper;
import ij.Macro;
import ij.Prefs;
import ij.plugin.frame.Recorder;
import java.awt.GridBagLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 */
public class SymmetricGaussianEstimatorUI implements IEstimatorUI {

  protected int fitradius;
  protected String method;
  protected double sigma;
  protected JTextField fitregsizeTextField;
  protected JComboBox<String> methodComboBox;
  protected JTextField sigmaTextField;
  protected int DEFAULT_FITRAD = 5;
  protected double DEFAULT_SIGMA = 1.6;
  protected static final String MLE = "Maximum likelihood";
  protected static final String LSQ = "Least squares";

  @Override
  public String getName() {
    return "2D Gaussian estimator";
  }

  @Override
  public JPanel getOptionsPanel() {
    fitregsizeTextField = new JTextField(Prefs.get("thunderstorm.estimators.fitregion", "" + DEFAULT_FITRAD), 20);
    methodComboBox = new JComboBox<String>(new String[]{LSQ, MLE});
    methodComboBox.setSelectedItem(Prefs.get("thunderstorm.estimators.method", LSQ));
    sigmaTextField = new JTextField(Prefs.get("thunderstorm.estimators.sigma", "" + DEFAULT_SIGMA));
    
    JPanel panel = new JPanel(new GridBagLayout());
    panel.add(new JLabel("Fitting radius [px]:"), GridBagHelper.leftCol());
    panel.add(fitregsizeTextField, GridBagHelper.rightCol());
    panel.add(new JLabel("Fitting method:"), GridBagHelper.leftCol());
    panel.add(methodComboBox, GridBagHelper.rightCol());
    panel.add(new JLabel("Initial sigma [px]:"), GridBagHelper.leftCol());
    panel.add(sigmaTextField, GridBagHelper.rightCol());
    
    return panel;
  }

  @Override
  public void readParameters() {
    fitradius = Integer.parseInt(fitregsizeTextField.getText());
    method = methodComboBox.getItemAt(methodComboBox.getSelectedIndex());
    sigma = Double.parseDouble(sigmaTextField.getText());
    
    Prefs.set("thunderstorm.estimators.fitregion", "" + fitradius);
    Prefs.set("thunderstorm.estimators.method", method);
    Prefs.set("thunderstorm.estimators.sigma", "" + sigma);
  }

  @Override
  public IEstimator getImplementation() {
    if(LSQ.equals(method)){
        LSQFitter fitter = new LSQFitter(new SymmetricGaussianPSF(sigma));
        return new MultipleLocationsImageFitting(fitradius / 2, fitter);
    }
    if(MLE.equals(method)){
        MLEFitter fitter = new MLEFitter(new SymmetricGaussianPSF(sigma));
        return new MultipleLocationsImageFitting(fitradius / 2, fitter);
    }
    throw new IllegalArgumentException("Unknown fitting method: "+ method);
  }

  @Override
  public void recordOptions() {
    if (fitradius != DEFAULT_FITRAD) {
      Recorder.recordOption("fitrad", Integer.toString(fitradius));
    }
    if(sigma != DEFAULT_SIGMA){
      Recorder.recordOption("sigma", Double.toString(sigma));
    }
    if(!LSQ.equals(method)){
      Recorder.recordOption("method", method);
    }
  }

  @Override
  public void readMacroOptions(String options) {
    fitradius = Integer.parseInt(Macro.getValue(options, "fitrad", Integer.toString(DEFAULT_FITRAD)));
    sigma = Double.parseDouble(Macro.getValue(options, "sigma", Double.toString(DEFAULT_SIGMA)));
    method = Macro.getValue(options, "method", LSQ);
  }

  @Override
  public void resetToDefaults() {
    fitregsizeTextField.setText(Integer.toString(DEFAULT_FITRAD));
    sigmaTextField.setText(Double.toString(DEFAULT_SIGMA));
    methodComboBox.setSelectedItem(LSQ);
  }
}
