#pragma version(1)
#pragma rs java_package_name(com.dundeeuni.Alistair.cvdvision)

rs_allocation lut;

uchar4 __attribute__((kernel)) invert(uchar4 in, uint32_t x, uint32_t y) 
{
	
	uchar4 out = in;
	uint index = 0;
	
	index = (in.b/4);
	index = index | ((in.g/4) << 6);
	index = index | ((in.r/4) << 12);

	out = rsGetElementAt_uchar4(lut, index);
	return out;
}