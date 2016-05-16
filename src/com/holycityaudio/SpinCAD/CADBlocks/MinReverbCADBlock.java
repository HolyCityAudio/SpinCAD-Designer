/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 */

// MinReverbCADBlock.java
// based on "minimum reverb" from Spin web site.

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class MinReverbCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5323444338681502405L;
	// local register defines
	private int apout;

	// coefficients
	private double krt = 0.13;  // reverb time
	private double kap = 0.325;  // AP coeff
	public MinReverbCADBlock(int x, int y) {
		super(x, y);
		// ---
		addInputPin(this);
		addOutputPin(this);
		addControlInputPin(this, "Reverb Time");
		setName("Min Reverb");
	}

	public void generateCode(SpinFXBlock sfxb) {
		//		super(blockName);
		int input;
		int output = -1;
		
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			if (input != -1) {		// don't generate code unless there is an input connection
				sfxb.FXallocDelayMem("api1", 122);
				sfxb.FXallocDelayMem("api2", 303);
				sfxb.FXallocDelayMem("api3", 553);
				sfxb.FXallocDelayMem("api4", 922);
				sfxb.FXallocDelayMem("ap1", 3823);
				sfxb.FXallocDelayMem("del1", 6512);
				sfxb.FXallocDelayMem("ap2", 4732);
				sfxb.FXallocDelayMem("del2", 5016);
				apout = sfxb.allocateReg();
				//		dry = sfxb.allocateReg();
				output = sfxb.allocateReg();
				
				sfxb.comment("Minimum reverb");
				
				sfxb.readRegister(input, 0.25);	

				sfxb.FXreadDelay("api1", 1.0, kap);  // read from the end of api1
				sfxb.FXwriteAllpass("api1", 0, -1.0);  // write back in inverted

				sfxb.FXreadDelay("api2", 1.0, kap);  // read from the end of api2
				sfxb.FXwriteAllpass("api2", 0, -1.0);  // write back in inverted

				sfxb.FXreadDelay("api3", 1.0, kap);  // read from the end of api3
				sfxb.FXwriteAllpass("api3", 0, -1.0);  // write back in inverted

				sfxb.FXreadDelay("api4", 1.0, kap);  // read from the end of api4
				sfxb.FXwriteAllpass("api4", 0, -1.0);  // write back in inverted

				sfxb.writeRegister(apout, 1.0);  // write apout, keep ACC

				// first loop delay
				int Control1 = -1;
				SpinCADPin pd = this.getPin("Reverb Time").getPinConnection();
				if(pd != null) {
					Control1 = pd.getRegister();
					sfxb.FXreadDelay("del2", 1.0, 1.0);  // read del2, scale by krt			
					sfxb.mulx(Control1);
				}
				else {
					sfxb.FXreadDelay("del2", 1.0, krt);  // read del2, scale by krt			
				}
				sfxb.FXreadDelay("ap1", 1.0, -kap);  // do loop ap
				sfxb.FXwriteAllpass("ap1", 0, kap);
				sfxb.FXwriteDelay("del1", 0, 1.99);
				//		sfxb.readRegister(dry,1.0);
				//		sfxb.writeRegister(output_right, 0.0);

				// second loop delay
				sfxb.readRegister(apout, 1.0);
				if(pd != null) {
					sfxb.FXreadDelay("del1", 1.0, 1.0);  // read del2, scale by krt			
					sfxb.mulx(Control1);
				}
				else {
					sfxb.FXreadDelay("del1", 1.0, krt);  // read del2, scale by krt
				}
				sfxb.FXreadDelay("ap2", 1.0, -kap);  // do loop ap
				sfxb.FXwriteAllpass("ap2", 0, kap);
				sfxb.FXwriteDelay("del2", 0, 1.99);
				//		sfxb.readRegister(dry,1.0);
				sfxb.writeRegister(output, 0.0);
				p = this.getPin("Audio Output 1");
				p.setRegister(output);		
			}
		}
		System.out.println("Min reverb code gen!");	
	}
}
