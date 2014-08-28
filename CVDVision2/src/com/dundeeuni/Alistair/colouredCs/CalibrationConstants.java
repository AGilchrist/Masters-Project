//Code belongs to Dr David Flatla

package com.dundeeuni.Alistair.colouredCs;

public interface CalibrationConstants
{
	// set to "" for real study (should write to appropriate folder then)
	public static final String OUTPUT_PATH = "/Users/drf976/Dropbox/2012_ASSETS_RealtimeSimulation/study/";
	//public static final String OUTPUT_PATH = "";

	public static final String[] PARTICIPANT_TYPES = { "CVD", "nonCVD", "CVDSimulation", "Protanope", "Deuteranope", "Tritanope" };

	// converted the Smith&Pokorny numbers from above into LUV_lch coordinates. This is their hue angle (except for L* = 0)
	public static final double PROTAN_LUV_HUE = 5.096813465946060;
	public static final double DEUTAN_LUV_HUE = 170.317865042132000;
	public static final double TRITAN_LUV_HUE = 277.503390777105000;

	// angles used for calibration (and names for them too)
	public static final double[] HUE_ANGLES = { PROTAN_LUV_HUE, PROTAN_LUV_HUE+180.0, DEUTAN_LUV_HUE, DEUTAN_LUV_HUE+180.0, TRITAN_LUV_HUE, TRITAN_LUV_HUE-180.0 };
	public static final String[] HUE_ANGLE_NAMES = { "PROT_TWRD", "PROT_AWAY", "DEUT_TWRD", "DEUT_AWAY", "TRIT_TWRD", "TRIT_AWAY" };

	public static final int NUM_LINES = 6;

	public static final long CIRCLE_SHOW_TIME = 4000;
	public static final long PROGRESS_SHOW_TIME = 1000;

	// colors to be displayed for calibration
	// this top set of colours are those generated by web_based_simulation/OutputLines.java
	public static final int[] PROT_TWRD = { 7763574, 7829110, 7894646, 7960182, 7959926, 8025462, 8025461, 8090997, 8156533, 8156277, 8221813, 8287349, 8352885, 8352629, 8418165, 8418164, 8483700, 8549236, 8548980, 8614516, 8680052, 8745588, 8745587, 8745331, 8810867, 8876403, 8941939, 8941683, 9007219, 9072755, 9072754, 9138290, 9138034, 9203570, 9269106, 9334642, 9334386, 9399922, 9399921, 9465457, 9465201, 9530737, 9596273, 9661809, 9661553, 9727089, 9727088, 9792624, 9792368, 9857904, 9923440, 9988976, 9988720, 9988719, 10054255, 10119791, 10119535, 10185071, 10250607, 10250351, 10250350, 10315886, 10381422, 10381166, 10446702, 10512238, 10512237, 10511981, 10577517, 10643053, 10642797, 10708333, 10773869, 10773868, 10773612, 10839148, 10904684, 10904428, 10969964, 11035500, 11035499, 11035243, 11100779, 11166315, 11166059, 11231595, 11231594, 11297130, 11296874, 11362410, 11427946, 11427690, 11493226, 11493225, 11492969, 11558505, 11624041, 11623785, 11689321, 11689320, 11754856, 11754600, 11820136, 11819880, 11885416, 11885415, 11950951, 11950695, 12016231, 12015975, 12081511, 12081510, 12147046, 12146790, 12212326, 12212070, 12277606, 12277605, 12343141, 12342885, 12408421, 12408165, 12473701, 12473700, 12473444, 12538980, 12604516, 12604260, 12669796, 12669795, 12669539, 12735075, 12734819, 12800355, 12865891, 12865890, 12865634, 12931170, 12930914, 12996450, 12996194, 12996193, 13061729, 13061473, 13127009, 13126753, 13192289, 13192288, 13192032, 13257568, 13323104, 13322848, 13322847, 13388383, 13388127, 13453663, 13453407, 13518943, 13518687, 13518686, 13584222, 13583966, 13649502, 13649246, 13649245, 13714781, 13714525, 13714269, 13779805, 13779549, 13845085, 13845084, 13844828, 13910364, 13910108, 13975644, 13975643, 13975387, 14040923, 14040667, 14106203, 14105947, 14105946, 14105690, 14171226, 14170970, 14236506, 14236250, 14236249, 14301785, 14301529, 14301273, 14366809, 14366553, 14366552, 14432088, 14431832, 14431576, 14497112, 14496856, 14496855, 14562391, 14562135, 14561879, 14627415, 14627159, 14627158, 14626902, 14692438, 14692182, 14691926, 14757462, 14757206, 14757205, 14822741, 14822485, 14822229, 14887765, 14887509, 14887253, 14887252, 14886996, 14952532, 14952276, 14952020, 15017556, 15017300, 15017299, 15017043, 15082579, 15082323, 15082067, 15081811, 15147347, 15147091, 15147090, 15146834, 15146578, 15212114, 15211858, 15211602, 15211346, 15211090, 15276626, 15276625, 15276369, 15276113, 15275857, 15275601, 15341137, 15340881, 15340625, 15340369, 15340113, 15340112, 15339856, 15339600, 15405136, 15404880, 15404624, 15404368, 15404112, 15403856, 15403600, 15403344, 15403088, 15402832, 15402576, 15402320, 15402064, 15467600, 15467344, 15467088, 15466832, 15466576, 15466575, 15532111, };
	public static final int[] PROT_AWAY = { 7763574, 7698038, 7698294, 7632758, 7632759, 7567223, 7501687, 7436151, 7436407, 7370871, 7305335, 7239799, 7239800, 7174264, 7174520, 7108984, 7043448, 6977912, 6912376, 6912632, 6847096, 6781560, 6781561, 6716025, 6650489, 6650745, 6585209, 6519673, 6454137, 6388601, 6323065, 6323321, 6257785, 6257786, 6192250, 6126714, 6061178, 5995642, 5995898, 5930362, 5864826, 5799290, 5733754, 5668218, 5668474, 5668475, 5602939, 5537403, 5471867, 5406331, 5340795, 5275259, 5275515, 5209979, 5144443, 5078907, 5013371, 5013372, 4947836, 4882300, 4882556, 4817020, 4751484, 4685948, 4620412, 4554876, 4489340, 4423804, 4358268, 4358524, 4292988, 4227452, 4161916, 4161917, 4096381, 4030845, 3965309, 3899773, 3834237, 3834493, 3768957, 3703421, 3637885, 3572349, 3506813, 3441277, 3375741, 3310205, 3244669, 3179133, 3113597, 3113853, 3113854, 3048318, 2982782, 2917246, 2851710, 2786174, 2720638, 2655102, 2589566, 2524030, 2458494, 2392958, 2327422, 2261886, 2196350, 2130814, 2131070, 2065534, 1999998, 1934462, 1868926, 1803390, 1737854, 1672318, 1606782, 1541246, 1475710, 1410174, 1344638, 1279102, 1213566, 1148030, 1082494, 1016958, 951422, 885886, 885887, 820351, 754815, 689279, 623743, 558207, 492671, 427135, 361599, 296063, 230527, 164991, 99455, 33919, 34175, };
	public static final int[] DEUT_TWRD = { 7763574, 7698038, 7698294, 7632758, 7567222, 7501686, 7501942, 7436406, 7370870, 7305334, 7239798, 7240054, 7174518, 7108982, 7043446, 6977910, 6978166, 6912630, 6912629, 6847093, 6781557, 6716021, 6716277, 6650741, 6585205, 6519669, 6454133, 6454389, 6388853, 6323317, 6257781, 6192245, 6192501, 6126965, 6061429, 5995893, 5930357, 5864821, 5865077, 5799541, 5734005, 5668469, 5602933, 5537397, 5537653, 5472117, 5472116, 5406580, 5341044, 5275508, 5209972, 5144436, 5144692, 5079156, 5013620, 4948084, 4882548, 4817012, 4751476, 4751732, 4686196, 4620660, 4555124, 4489588, 4424052, 4358516, 4292980, 4293236, 4227700, 4162164, 4096628, 4031092, 3965556, 3900020, 3834484, 3768948, 3703412, 3703668, 3638132, 3572596, 3507060, 3441524, 3375988, 3310452, 3244916, 3179380, 3179379, 3113843, 3048307, 3048563, 2983027, 2917491, 2851955, 2786419, 2720883, 2655347, 2589811, 2524275, 2458739, 2393203, 2327667, 2262131, 2196595, 2131059, 2131315, 2065779, 2000243, 1934707, 1869171, 1803635, 1738099, 1672563, 1607027, 1541491, 1475955, 1410419, 1344883, 1279347, 1213811, 1148275, 1082739, 1017203, 951667, 886131, 820595, 755059, 689523, 623987, 558451, 492915, 427379, 361843, 296307, 230771, 165235, 99699, 34163, 34419, };
	public static final int[] DEUT_AWAY = { 7763574, 7829110, 7894646, 7894390, 7959926, 8025462, 8090998, 8156534, 8156535, 8156279, 8221815, 8287351, 8352887, 8352631, 8418167, 8483703, 8483447, 8548983, 8614519, 8680055, 8679799, 8745335, 8810871, 8876407, 8876151, 8941687, 9007223, 9072759, 9072503, 9138039, 9138040, 9203576, 9203320, 9268856, 9334392, 9399928, 9399672, 9465208, 9530744, 9530488, 9596024, 9661560, 9661304, 9726840, 9792376, 9792120, 9857656, 9923192, 9988728, 9988472, 10054008, 10054009, 10119545, 10119289, 10184825, 10250361, 10250105, 10315641, 10381177, 10380921, 10446457, 10511993, 10511737, 10577273, 10642809, 10642553, 10708089, 10707833, 10773369, 10838905, 10838649, 10838650, 10904186, 10969722, 10969466, 11035002, 11100538, 11100282, 11165818, 11165562, 11231098, 11296634, 11296378, 11361914, 11427450, 11427194, 11492730, 11492474, 11558010, 11623546, 11623290, 11623291, 11688827, 11688571, 11754107, 11819643, 11819387, 11884923, 11884667, 11950203, 11949947, 12015483, 12081019, 12080763, 12146299, 12146043, 12211579, 12211323, 12276859, 12342395, 12342139, 12342140, 12407676, 12407420, 12472956, 12472700, 12538236, 12537980, 12603516, 12603260, 12668796, 12668540, 12734076, 12799612, 12799356, 12864892, 12864636, 12930172, 12929916, 12995452, 12995196, 12995197, 13060733, 13060477, 13126013, 13125757, 13191293, 13191037, 13256573, 13256317, 13321853, 13321597, 13321341, 13386877, 13386621, 13452157, 13451901, 13517437, 13517181, 13582717, 13582461, 13647997, 13647741, 13647742, 13713022, 13712766, 13778302, 13778046, 13843582, 13843326, 13843070, 13908606, 13908350, 13973886, 13973630, 14039166, 14038910, 14038654, 14104190, 14103934, 14103678, 14169214, 14168958, 14234494, 14234238, 14233982, 14233983, 14299519, 14299263, 14299007, 14364543, 14364287, 14364031, 14429567, 14429311, 14429055, 14494591, 14494335, 14494079, 14559615, 14559359, 14559103, 14558847, 14624383, 14624127, 14623871, 14689407, 14689151, 14688895, 14688639, 14754175, 14753919, 14753663, 14753407, 14818943, 14818687, 14818431, 14818175, 14818176, 14817920, 14883456, 14883200, 14882944, 14882688, 14882432, 14882176, 14947712, 14947456, 14947200, 14946944, 14946688, 14946432, 14946176, 15011712, 15011456, 15011200, 15010944, 15010688, 15010432, 15010176, 15009920, 15009664, 15009408, 15009152, 15008896, 15008640, 15008384, 15008128, 15007872, 15073408, 15138944, };
	public static final int[] TRIT_TWRD = { 7763574, 7763575, 7763576, 7829112, 7829113, 7829114, 7829115, 7828859, 7828860, 7828861, 7828862, 7894398, 7894399, 7894400, 7894401, 7894145, 7894146, 7894147, 7894148, 7959684, 7959685, 7959686, 7959687, 7959431, 7959432, 7959433, 8024969, 8024970, 8024971, 8024972, 8024716, 8024717, 8024718, 8090254, 8090255, 8090256, 8090257, 8090001, 8090002, 8090003, 8155539, 8155540, 8155541, 8155542, 8155286, 8155287, 8155288, 8220824, 8220825, 8220826, 8220570, 8220571, 8220572, 8220573, 8286109, 8286110, 8286111, 8285855, 8285856, 8285857, 8351393, 8351394, 8351395, 8351139, 8351140, 8351141, 8351142, 8416678, 8416679, 8416423, 8416424, 8416425, 8416426, 8481962, 8481963, 8481707, 8481708, 8481709, 8481710, 8547246, 8547247, 8546991, 8546992, 8546993, 8546994, 8546738, 8546739, 8612275, 8612276, 8612277, 8612278, 8612022, 8612023, 8677559, 8677560, 8677561, 8677305, 8677306, 8677307, 8742843, 8742844, 8742845, 8742589, 8742590, 8742591, 8808127, 8808128, 8807872, 8807873, 8807874, 8807875, 8873411, 8873155, 8873156, 8873157, 8873158, 8872902, 8872903, 8938439, 8938440, 8938441, 8938185, 8938186, 9003722, 9003723, 9003467, 9003468, 9003469, 9003470, 9003214, 9068750, 9068751, 9068752, 9068753, 9068497, 9068498, 9134034, 9134035, 9133779, 9133780, 9133781, 9133782, 9199318, 9199062, 9199063, 9199064, 9198808, 9198809, 9264345, 9264346, 9264347, 9264091, 9264092, 9264093, 9329629, 9329373, 9329374, 9329375, 9329119, 9329120, 9394656, 9394657, 9394401, 9394402, 9394403, 9394404, 9394148, 9459684, 9459685, 9459686, 9459430, 9459431, 9524967, 9524968, 9524712, 9524713, 9524714, 9524458, 9524459, 9589995, 9589996, 9589740, 9589741, 9589742, 9589486, 9655022, 9655023, 9654767, 9654768, 9654769, 9654513, 9654514, 9720050, 9720051, 9719795, 9719796, 9719797, 9719541, 9785077, 9785078, 9784822, 9784823, 9784824, 9784568, 9850104, 9850105, 9850106, 9849850, 9849851, 9849595, 9915131, 9915132, 9915133, 9914877, 9914878, 9914622, 9914623, 9980159, 9979903, };
	public static final int[] TRIT_AWAY = { 7763574, 7763573, 7763572, 7763828, 7763827, 7763826, 7698290, 7698289, 7698288, 7698287, 7698286, 7698285, 7698541, 7698540, 7633004, 7633003, 7633002, 7633001, 7633000, 7632999, 7632998, 7632997, 7633253, 7567717, 7567716, 7567715, 7567714, 7567713, 7567712, 7567711, 7567710, 7567709, 7502173, 7502172, 7502428, 7502427, 7502426, 7502425, 7502424, 7502423, 7502422, 7502421, 7436885, 7436884, 7436883, 7436882, 7437138, 7437137, 7437136, 7437135, 7437134, 7437133, 7437132, 7371596, 7371595, 7371594, 7371593, 7371592, 7371591, 7371847, 7371846, 7371845, 7371844, 7371843, 7371842, 7371841, 7306305, 7306304, 7306303, 7306302, 7306301, 7306300, 7306299, 7306298, 7306297, 7306296, 7306552, 7306551, 7306550, 7306549, 7306548, 7241012, 7241011, 7241010, 7241009, 7241008, 7241007, 7241006, 7241005, 7241004, 7241003, 7241002, 7241001, 7241000, 7240999, 7240998, 7240997, 7240996, 7240995, 7240994, 7240993, 7175457, 7175456, 7175712, 7175711, 7175710, 7175709, 7175708, 7175707, 7175706, 7175705, 7175704, 7175703, 7175702, 7175701, 7175700, 7175699, 7175698, 7175697, 7175696, 7175695, 7175694, 7175693, 7175692, 7175691, 7175690, 7175689, 7175688, 7175687, 7175686, 7175685, 7175684, 7175683, 7175682, 7175680, };
//	public static final int[] PROT_TWRD = { 7763574, 7829110, 7894646, 7960182, 7959926, 8025462, 8025461, 8090997, 8156533, 8156277, 8221813, 8287349, 8352885, 8352629, 8418165, 8418164, 8483700, 8549236, 8548980, 8614516, 8680052, 8745588, 8745587, 8745331, 8810867, 8876403, 8941939, 8941683, 9007219, 9072755, 9072754, 9138290, 9138034, 9203570, 9269106, 9334642, 9334386, 9399922, 9399921, 9465457, 9465201, 9530737, 9596273, 9661809, 9661553, 9727089, 9727088, 9792624, 9792368, 9857904, 9923440, 9988976, 9988720, 9988719, 10054255, 10119791, 10119535, 10185071, 10250607, 10250351, 10250350, 10315886, 10381422, 10381166, 10446702, 10512238, 10512237, 10511981, 10577517, 10643053, 10642797, 10708333, 10773869, 10773868, 10773612, 10839148, 10904684, 10904428, 10969964, 11035500, 11035499, 11035243, 11100779, 11166315, 11166059, 11231595, 11231594, 11297130, 11296874, 11362410, 11427946, 11427690, 11493226, 11493225, 11492969, 11558505, 11624041, 11623785, 11689321, 11689320, 11754856, 11754600, 11820136, 11819880, 11885416, 11885415, 11950951, 11950695, 12016231, 12015975, 12081511, 12081510, 12147046, 12146790, 12212326, 12212070, 12277606, 12277605, 12343141, 12342885, 12408421, 12408165, 12473701, 12473700, 12473444, 12538980, 12604516, 12604260, 12669796, 12669795, 12669539, 12735075, 12734819, 12800355, 12865891, 12865890, 12865634, 12931170, 12930914, 12996450, 12996194, 12996193, 13061729, 13061473, 13127009, 13126753, 13192289, 13192288, 13192032, 13257568, 13323104, 13322848, 13322847, 13388383, 13388127, 13453663, 13453407, 13518943, 13518687, 13518686, 13584222, 13583966, 13649502, 13649246, 13649245, 13714781, 13714525, 13714269, 13779805, 13779549, 13845085, 13845084, 13844828, 13910364, 13910108, 13975644, 13975643, 13975387, 14040923, 14040667, 14106203, 14105947, 14105946, 14105690, 14171226, 14170970, 14236506, 14236250, 14236249, 14301785, 14301529, 14301273, 14366809, 14366553, 14366552, 14432088, 14431832, 14431576, 14497112, 14496856, 14496855, 14562391, 14562135, 14561879, 14627415, 14627159, 14627158, 14626902, 14692438, 14692182, 14691926, 14757462, 14757206, 14757205, 14822741, 14822485, 14822229, 14887765, 14887509, 14887253, 14887252, 14886996, 14952532, 14952276, 14952020, 15017556, 15017300, 15017299, 15017043, 15082579, 15082323, 15082067, 15081811, 15147347, 15147091, 15147090, 15146834, 15146578, 15212114, 15211858, 15211602, 15211346, 15211090, 15276626, 15276625, 15276369, 15276113, 15275857, 15275601, 15341137, 15340881, 15340625, 15340369, 15340113, 15340112, 15339856, 15339600, 15405136, 15404880, 15404624, 15404368, 15404112, 15403856, 15403600, 15403344, 15403088, 15402832, 15402576, 15402320, 15402064, 15467600, 15467344, 15467088, 15466832, 15466576, 15466575, };
//	public static final int[] PROT_AWAY = { 7763574, 7698038, 7698294, 7632758, 7632759, 7567223, 7501687, 7436151, 7436407, 7370871, 7305335, 7239799, 7239800, 7174264, 7174520, 7108984, 7043448, 6977912, 6912376, 6912632, 6847096, 6781560, 6781561, 6716025, 6650489, 6650745, 6585209, 6519673, 6454137, 6388601, 6323065, 6323321, 6257785, 6257786, 6192250, 6126714, 6061178, 5995642, 5995898, 5930362, 5864826, 5799290, 5733754, 5668218, 5668474, 5668475, 5602939, 5537403, 5471867, 5406331, 5340795, 5275259, 5275515, 5209979, 5144443, 5078907, 5013371, 5013372, 4947836, 4882300, 4882556, 4817020, 4751484, 4685948, 4620412, 4554876, 4489340, 4423804, 4358268, 4358524, 4292988, 4227452, 4161916, 4161917, 4096381, 4030845, 3965309, 3899773, 3834237, 3834493, 3768957, 3703421, 3637885, 3572349, 3506813, 3441277, 3375741, 3310205, 3244669, 3179133, 3113597, 3113853, 3113854, 3048318, 2982782, 2917246, 2851710, 2786174, 2720638, 2655102, 2589566, 2524030, 2458494, 2392958, 2327422, 2261886, 2196350, 2130814, 2131070, 2065534, 1999998, 1934462, 1868926, 1803390, 1737854, 1672318, 1606782, 1541246, 1475710, 1410174, 1344638, 1279102, 1213566, 1148030, 1082494, 1016958, 951422, 885886, 885887, 820351, 754815, 689279, 623743, 558207, 492671, 427135, 361599, 296063, 230527, 164991, 99455, 33919, 34175, };
//	public static final int[] DEUT_TWRD = { 7763574, 7698038, 7698294, 7632758, 7567222, 7501686, 7501942, 7436406, 7370870, 7305334, 7239798, 7240054, 7174518, 7108982, 7043446, 6977910, 6978166, 6912630, 6912629, 6847093, 6781557, 6716021, 6716277, 6650741, 6585205, 6519669, 6454133, 6454389, 6388853, 6323317, 6257781, 6192245, 6192501, 6126965, 6061429, 5995893, 5930357, 5864821, 5865077, 5799541, 5734005, 5668469, 5602933, 5537397, 5537653, 5472117, 5472116, 5406580, 5341044, 5275508, 5209972, 5144436, 5144692, 5079156, 5013620, 4948084, 4882548, 4817012, 4751476, 4751732, 4686196, 4620660, 4555124, 4489588, 4424052, 4358516, 4292980, 4293236, 4227700, 4162164, 4096628, 4031092, 3965556, 3900020, 3834484, 3768948, 3703412, 3703668, 3638132, 3572596, 3507060, 3441524, 3375988, 3310452, 3244916, 3179380, 3179379, 3113843, 3048307, 3048563, 2983027, 2917491, 2851955, 2786419, 2720883, 2655347, 2589811, 2524275, 2458739, 2393203, 2327667, 2262131, 2196595, 2131059, 2131315, 2065779, 2000243, 1934707, 1869171, 1803635, 1738099, 1672563, 1607027, 1541491, 1475955, 1410419, 1344883, 1279347, 1213811, 1148275, 1082739, 1017203, 951667, 886131, 820595, 755059, 689523, 623987, 558451, 492915, 427379, 361843, 296307, 230771, 165235, 99699, 34163, 34419, };
//	public static final int[] DEUT_AWAY = { 7763574, 7829110, 7894646, 7894390, 7959926, 8025462, 8090998, 8156534, 8156535, 8156279, 8221815, 8287351, 8352887, 8352631, 8418167, 8483703, 8483447, 8548983, 8614519, 8680055, 8679799, 8745335, 8810871, 8876407, 8876151, 8941687, 9007223, 9072759, 9072503, 9138039, 9138040, 9203576, 9203320, 9268856, 9334392, 9399928, 9399672, 9465208, 9530744, 9530488, 9596024, 9661560, 9661304, 9726840, 9792376, 9792120, 9857656, 9923192, 9988728, 9988472, 10054008, 10054009, 10119545, 10119289, 10184825, 10250361, 10250105, 10315641, 10381177, 10380921, 10446457, 10511993, 10511737, 10577273, 10642809, 10642553, 10708089, 10707833, 10773369, 10838905, 10838649, 10838650, 10904186, 10969722, 10969466, 11035002, 11100538, 11100282, 11165818, 11165562, 11231098, 11296634, 11296378, 11361914, 11427450, 11427194, 11492730, 11492474, 11558010, 11623546, 11623290, 11623291, 11688827, 11688571, 11754107, 11819643, 11819387, 11884923, 11884667, 11950203, 11949947, 12015483, 12081019, 12080763, 12146299, 12146043, 12211579, 12211323, 12276859, 12342395, 12342139, 12342140, 12407676, 12407420, 12472956, 12472700, 12538236, 12537980, 12603516, 12603260, 12668796, 12668540, 12734076, 12799612, 12799356, 12864892, 12864636, 12930172, 12929916, 12995452, 12995196, 12995197, 13060733, 13060477, 13126013, 13125757, 13191293, 13191037, 13256573, 13256317, 13321853, 13321597, 13321341, 13386877, 13386621, 13452157, 13451901, 13517437, 13517181, 13582717, 13582461, 13647997, 13647741, 13647742, 13647486, 13713022, 13712766, 13778302, 13778046, 13843582, 13843326, 13843070, 13908606, 13908350, 13973886, 13973630, 14039166, 14038910, 14038654, 14104190, 14103934, 14103678, 14169214, 14168958, 14234494, 14234238, 14233982, 14233983, 14299519, 14299263, 14299007, 14364543, 14364287, 14364031, 14429567, 14429311, 14429055, 14494591, 14494335, 14494079, 14559615, 14559359, 14559103, 14558847, 14624383, 14624127, 14623871, 14689407, 14689151, 14688895, 14688639, 14754175, 14753919, 14753663, 14753407, 14818943, 14818687, 14818431, 14818175, 14818176, 14817920, 14883456, 14883200, 14882944, 14882688, 14882432, 14882176, 14947712, 14947456, 14947200, 14946944, 14946688, 14946432, 14946176, 15011712, 15011456, 15011200, 15010944, 15010688, 15010432, 15010176, 15009920, 15009664, 15009408, 15009152, 15008896, 15008640, 15008384, 15008128, 15007872, 15073408, };
//	public static final int[] TRIT_TWRD = { 7763574, 7763575, 7763576, 7829112, 7829113, 7829114, 7829115, 7828859, 7828860, 7828861, 7828862, 7894398, 7894399, 7894400, 7894401, 7894145, 7894146, 7894147, 7894148, 7959684, 7959685, 7959686, 7959687, 7959431, 7959432, 7959433, 8024969, 8024970, 8024971, 8024972, 8024716, 8024717, 8024718, 8090254, 8090255, 8090256, 8090257, 8090001, 8090002, 8090003, 8155539, 8155540, 8155541, 8155542, 8155286, 8155287, 8155288, 8220824, 8220825, 8220826, 8220570, 8220571, 8220572, 8220573, 8286109, 8286110, 8286111, 8285855, 8285856, 8285857, 8351393, 8351394, 8351395, 8351139, 8351140, 8351141, 8351142, 8416678, 8416679, 8416423, 8416424, 8416425, 8416426, 8481962, 8481963, 8481707, 8481708, 8481709, 8481710, 8547246, 8547247, 8546991, 8546992, 8546993, 8546994, 8546738, 8546739, 8612275, 8612276, 8612277, 8612278, 8612022, 8612023, 8677559, 8677560, 8677561, 8677305, 8677306, 8677307, 8742843, 8742844, 8742845, 8742589, 8742590, 8742591, 8808127, 8808128, 8807872, 8807873, 8807874, 8807875, 8873411, 8873155, 8873156, 8873157, 8873158, 8872902, 8872903, 8938439, 8938440, 8938441, 8938185, 8938186, 9003722, 9003723, 9003467, 9003468, 9003469, 9003470, 9003214, 9068750, 9068751, 9068752, 9068753, 9068497, 9068498, 9134034, 9134035, 9133779, 9133780, 9133781, 9133782, 9199318, 9199062, 9199063, 9199064, 9198808, 9198809, 9264345, 9264346, 9264347, 9264091, 9264092, 9264093, 9329629, 9329373, 9329374, 9329375, 9329119, 9329120, 9394656, 9394657, 9394401, 9394402, 9394403, 9394404, 9394148, 9459684, 9459685, 9459686, 9459430, 9459431, 9524967, 9524968, 9524712, 9524713, 9524714, 9524458, 9524459, 9589995, 9589996, 9589740, 9589741, 9589742, 9589486, 9655022, 9655023, 9654767, 9654768, 9654769, 9654513, 9654514, 9720050, 9720051, 9719795, 9719796, 9719797, 9719541, 9785077, 9785078, 9784822, 9784823, 9784824, 9784568, 9850104, 9850105, 9850106, 9849850, 9849851, 9849595, 9915131, 9915132, 9915133, 9914877, 9914878, 9914622, 9914623, 9980159, 9979903, };
//	public static final int[] TRIT_AWAY = { 7763574, 7763573, 7763572, 7763828, 7763827, 7763826, 7698290, 7698289, 7698288, 7698287, 7698286, 7698285, 7698541, 7698540, 7633004, 7633003, 7633002, 7633001, 7633000, 7632999, 7632998, 7632997, 7633253, 7567717, 7567716, 7567715, 7567714, 7567713, 7567712, 7567711, 7567710, 7567709, 7502173, 7502172, 7502428, 7502427, 7502426, 7502425, 7502424, 7502423, 7502422, 7502421, 7436885, 7436884, 7436883, 7436882, 7437138, 7437137, 7437136, 7437135, 7437134, 7437133, 7437132, 7371596, 7371595, 7371594, 7371593, 7371592, 7371591, 7371847, 7371846, 7371845, 7371844, 7371843, 7371842, 7371841, 7306305, 7306304, 7306303, 7306302, 7306301, 7306300, 7306299, 7306298, 7306297, 7306296, 7306552, 7306551, 7306550, 7306549, 7306548, 7241012, 7241011, 7241010, 7241009, 7241008, 7241007, 7241006, 7241005, 7241004, 7241003, 7241002, 7241001, 7241000, 7240999, 7240998, 7240997, 7240996, 7240995, 7240994, 7240993, 7175457, 7175456, 7175712, 7175711, 7175710, 7175709, 7175708, 7175707, 7175706, 7175705, 7175704, 7175703, 7175702, 7175701, 7175700, 7175699, 7175698, 7175697, 7175696, 7175695, 7175694, 7175693, 7175692, 7175691, 7175690, 7175689, 7175688, 7175687, 7175686, 7175685, 7175684, 7175683, 7175682, 7175681, 7175680, };
	public static final int[][] LINE_COLORS = { PROT_TWRD, PROT_AWAY, DEUT_TWRD, DEUT_AWAY, TRIT_TWRD, TRIT_AWAY };

//	public static final double[] RLM_VALUES = { -12.50, -6.25, 0.0, 6.25, 12.50 };
	public static final double[] RLM_VALUES = { -10.0, -6.6, -3.3, 0.0, 3.3, 6.6, 10.0 };
//	public static final double[] RLM_VALUES = { -12.50, -10.42, -8.33, -6.25, -4.17, -2.08, 0.0, 2.08, 4.17, 6.25, 8.33, 10.42, 12.50 };
	public static final double BASE_LUM = 50.0;

	public static final boolean OUTPUT_TO_TERMINAL = false;
	public static final boolean OUTPUT_TO_FILE = true;

	public static final int SIZE = 8;
	public static final int GAP = 8;

	public static final int TT_MASK = 0;
	public static final int TR_MASK = 1;
	public static final int RR_MASK = 2;
	public static final int BR_MASK = 3;
	public static final int BB_MASK = 4;
	public static final int BL_MASK = 5;
	public static final int LL_MASK = 6;
	public static final int TL_MASK = 7;

	public static final String FILE_COMPLETE = "Done!";

	public static final String INSTRUCTIONS =
		"In this study, we are measuring your ability to perceive variations in color.\n" +
		"You will see a shimmering square of dots. Inside this square, you will see a\n" +
		"circle with a gap in it. Please indicate which part of the circle is missing\n" +
		"using the numeric keypad. The keys are labelled accordingly. You will only see\n" +
		"the circle for " + CIRCLE_SHOW_TIME/1000.0  + " seconds, so make sure to pay attention. If you do not see a circle,\n" +
		"just press the 'space' bar. After every entry, you will be given a " + PROGRESS_SHOW_TIME/1000.0 + " second\n" +
		"break. During this break you can see your overall progress indicated on a\n" +
		"progress bar at the top. Please be careful when entering your choices, as there\n" +
		"is no 'undo' function.";
}