/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * SlicerCADBlock.java
 * Copyright (C) 2015 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
 * 
 *   This program is free software: you can redistribute it and/or modify 
 *   it under the terms of the GNU General Public License as published by 
 *   the Free Software Foundation, either version 3 of the License, or 
 *   (at your option) any later version. 
 * 
 *   This program is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 *   GNU General Public License for more details. 
 * 
 *   You should have received a copy of the GNU General Public License 
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *     
 */ 
		package com.holycityaudio.SpinCAD.CADBlocks;

		import java.awt.Color;
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
 		import com.holycityaudio.SpinCAD.ControlPanel.SlicerControlPanel;
		
	    @SuppressWarnings("unused")
	    public class SlicerCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private SlicerControlPanel cp = null;
			
			private double slice = 0.5;
			private double controlRange = 0;
			private int output;
			private int hold;

			public SlicerCADBlock(int x, int y) {
				super(x, y);
				setName("Slicer");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Control In");
				addControlInputPin(this, "Slice Level");
				addControlOutputPin(this, "Slicer Out");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new SlicerControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly

			
			sfxb.comment(getName());
			
			SpinCADPin sp = null;
					
			// Iterate through pin definitions and connect or assign as needed
			sp = this.getPin("Control In").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Slice Level").getPinConnection();
			int level = -1;
			if(sp != null) {
				level = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			hold = sfxb.allocateReg();
			if(this.getPin("Control In").isConnected() == true) {
			sfxb.scaleOffset(0.0, slice);
			if(this.getPin("Slice Level").isConnected() == true) {
			sfxb.mulx(level);
			}
			
			sfxb.readRegister(input1, -1.0);
			if(controlRange == 0) {
			sfxb.skip(NEG, 2);
			sfxb.scaleOffset(0, 0.9990234275);
			sfxb.skip(RUN, 1);
			sfxb.clear();
			sfxb.writeRegister(output, 0);
			} else {
			sfxb.skip(NEG, 2);
			sfxb.scaleOffset(0, 0.9990234275);
			sfxb.skip(RUN, 1);
			sfxb.scaleOffset(0, -0.9990234275);
			sfxb.writeRegister(output, 0);
			}
			
			}
			
			this.getPin("Slicer Out").setRegister(output);

			}
			
			// create setters and getter for control panel variables
			public void setslice(double __param) {
				slice = __param;	
			}
			
			public double getslice() {
				return slice;
			}
			public void setcontrolRange(int __param) {
				controlRange = (double) __param;	
			}
			
			public int getcontrolRange() {
				return (int) controlRange;
			}
		}	
