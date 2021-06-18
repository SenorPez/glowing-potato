import { Injectable } from '@angular/core';
import * as THREE from 'three';

@Injectable({
  providedIn: 'root'
})
export class OrbitdataService {

  testData = [
    [
      0.26545434248468586,
      0.08738021598632184,
      0.00011223491434057905
    ],
    [
      -0.5423695124380445,
      0.023176482170049893,
      -0.007899821430635437
    ],
    [
      -0.3747803048821346,
      -0.789022466028093,
      0
    ],
    [
      -0.05589534000620553,
      -1.0144850694026708,
      0.00004946737211370695
    ]
  ];

  arcDataX = [
    [
      0.26545434248468586,
      0.2552330585594254,
      0.24137977569464028,
      0.22420601431070444,
      0.2040559637205632,
      0.18129491304353493,
      0.15629913915529017,
      0.12944745391517393,
      0.10111443486739301,
      0.0716652389927514,
      0.041451823547578065,
      0.010810362096927914,
      -0.01994036340486538,
      -0.05049980227831213,
      -0.08058589251062237,
      -0.10993489554406774,
      -0.13830094129144657,
      -0.16545539122196004,
      -0.19118610547845283,
      -0.21529668166020263,
      -0.2376057181320851,
      -0.2579461432203091,
      -0.27616464302871824,
      -0.29212121441774563,
      -0.30568886550433916,
      -0.31675348346779353,
      -0.32521388811285323,
      -0.33098208921516786,
      -0.33398376580795325,
      -0.33415898591325,
      -0.3314631853690422,
      -0.32586842386420634,
      -0.31736493445347447,
      -0.30596297890539315,
      -0.29169501425100064,
      -0.2746181646415851,
      -0.2548169756428456,
      -0.2324064037917968,
      -0.20753496099872903,
      -0.18038788986543236,
      -0.15119019167429748,
      -0.12020926469144373,
      -0.08775684007428901,
      -0.05418983331353582,
      -0.019909672740908435,
      0.01464035978924737,
      0.0489802162684175,
      0.08260094054008485,
      0.11497545225446394,
      0.1455721320704242,
      0.17387072974055176,
      0.19937973580569085,
      0.22165402025526557,
      0.24031132905606792,
      0.2550462106799681,
      0.2656401506302229,
      0.27196709935568203,
      0.2739941123626024,
      0.2717773739972504,
      0.2654543424846874
    ],
    [
      -0.5423695124380445,
      -0.5433817884405143,
      -0.538104133396599,
      -0.5266564255415892,
      -0.5092273714395714,
      -0.4860697333326436,
      -0.4574950887756228,
      -0.42386827381729314,
      -0.385601642239197,
      -0.34314925250947753,
      -0.29700107258508984,
      -0.2476772716389936,
      -0.19572264806456008,
      -0.1417012252953732,
      -0.08619103144162699,
      -0.029779065656595882,
      0.026943556456191533,
      0.08338629424885749,
      0.13896373194121348,
      0.19310001237122662,
      0.24523318148624845,
      0.2948194700864752,
      0.3413375370704556,
      0.38429269435313435,
      0.4232211277682093,
      0.45769412064303555,
      0.48732227737029976,
      0.5117597332361773,
      0.5307083240864777,
      0.5439216752921763,
      0.5512091541839722,
      0.5524396140887021,
      0.5475448419091232,
      0.5365226056388557,
      0.5194391842923674,
      0.496431251649002,
      0.46770697830414315,
      0.4335462152113566,
      0.3942996275691146,
      0.350386661754416,
      0.3022922508612732,
      0.2505621965493245,
      0.19579720589315514,
      0.1386456104518906,
      0.07979484863968732,
      0.019961848630236725,
      -0.04011749624094656,
      -0.09969951835623758,
      -0.15804435199158598,
      -0.21442758703407735,
      -0.268151681301889,
      -0.318556818231691,
      -0.36503091913557467,
      -0.4070185567671961,
      -0.4440285666440256,
      -0.47564021058010586,
      -0.5015078088162866,
      -0.5213638186069807,
      -0.5350203941796223,
      -0.5423695124380438
    ],
    [
      -0.3747803048821346,
      -0.28168192412463294,
      -0.1848302719571744,
      -0.08560201840968676,
      0.014690675490885676,
      0.1148161852697138,
      0.21363362489184803,
      0.3100982227837926,
      0.4032629020219569,
      0.4922770288177543,
      0.5763831737807428,
      0.6549125890330779,
      0.7272799625838517,
      0.7929778821229386,
      0.8515713297091021,
      0.9026924384683602,
      0.9460356715043695,
      0.9813535295312997,
      1.0084528544787628,
      1.027191768686722,
      1.0374772707076834,
      1.03926349690479,
      1.0325506510542086,
      1.01738460040329,
      0.9938571347053666,
      0.9621068833939003,
      0.9223208840762802,
      0.8747367916925248,
      0.8196457106513453,
      0.7573956204821162,
      0.6883953472265514,
      0.6131190058326953,
      0.5321108008263321,
      0.4459900209514293,
      0.3554559957975381,
      0.261292696707842,
      0.1643725598258133,
      0.06565898782884161,
      -0.03379314537239402,
      -0.1328397938917707,
      -0.23025375989576707,
      -0.32473540973258747,
      -0.41492902874495236,
      -0.4994455867888958,
      -0.5768923329499719,
      -0.6459090716520737,
      -0.7052101979747667,
      -0.7536306465079649,
      -0.7901729609947905,
      -0.8140519071269002,
      -0.8247326410372331,
      -0.8219585923108594,
      -0.8057660030124908,
      -0.7764834124030319,
      -0.7347160648016391,
      -0.6813169186996897,
      -0.6173473165885512,
      -0.5440311975790413,
      -0.4627069126245911,
      -0.37478030488215186
    ],
    [
      -0.05589534000620553,
      0.04883428718777382,
      0.1530371200780252,
      0.2555896994895133,
      0.3553863656052593,
      0.45134939227973664,
      0.542438939253125,
      0.6276627985496486,
      0.7060859060760996,
      0.776839581781843,
      0.8391304519699921,
      0.8922489957948981,
      0.9355776450758767,
      0.968598352824267,
      0.9908995319521327,
      1.0021822522505413,
      1.0022655717389313,
      0.9910908688381873,
      0.9687250355128573,
      0.9353623895931289,
      0.8913251679186491,
      0.8370624716316816,
      0.7731475515808804,
      0.700273345791699,
      0.6192462123526239,
      0.5309778394477577,
      0.4364753587194481,
      0.33682973723134585,
      0.23320257508843337,
      0.1268114879011009,
      0.01891430311723047,
      -0.08920765597253828,
      -0.19626688768315578,
      -0.30098729296353605,
      -0.4021216670283086,
      -0.49846874654470824,
      -0.5888894686177407,
      -0.6723221219499069,
      -0.7477961061880507,
      -0.8144440605825398,
      -0.871512175109577,
      -0.918368553284228,
      -0.9545095530773632,
      -0.979564087848913,
      -0.9932959205691986,
      -0.9956040298361946,
      -0.9865211639016351,
      -0.9662107282675759,
      -0.9349621731563574,
      -0.8931850595397164,
      -0.8414019870957229,
      -0.780240565406603,
      -0.7104246020696818,
      -0.632764669411345,
      -0.5481481964218988,
      -0.4575292155505367,
      -0.36191787619461846,
      -0.26236981901744844,
      -0.15997548841968806,
      -0.05584944519742862
    ]
  ];

  arcDataY = [
    [
      0.08738021598632184,
      0.12093326764850275,
      0.1527684291787018,
      0.18250533765994506,
      0.2098230595585393,
      0.23445982076841507,
      0.256210715034165,
      0.27492399923725563,
      0.29049652158013356,
      0.30286873453324886,
      0.3120196406253853,
      0.3179619206225466,
      0.32073740864364486,
      0.3204130105533875,
      0.3170771105117626,
      0.3108364739556524,
      0.301813630871735,
      0.29014470821078875,
      0.2759776721657597,
      0.25947093767876656,
      0.24079230228463006,
      0.22011816297382028,
      0.1976329772317806,
      0.17352893211208428,
      0.14800578764260905,
      0.12127086269099692,
      0.09353913236785183,
      0.06503340588652426,
      0.035984552343250105,
      0.006631738931308655,
      -0.02277735853018788,
      -0.05198641936344701,
      -0.08073047120151168,
      -0.10873607823156974,
      -0.13572180274612355,
      -0.16139903243974274,
      -0.18547328101797728,
      -0.20764608436419793,
      -0.22761762656462364,
      -0.24509023621701317,
      -0.25977288904343593,
      -0.2713868319191223,
      -0.27967239896712465,
      -0.2843970150758566,
      -0.28536427023351796,
      -0.282423797712192,
      -0.27548150620071177,
      -0.26450951749128926,
      -0.24955497773560992,
      -0.230746784429136,
      -0.20829925182080863,
      -0.18251186622919757,
      -0.15376457823511797,
      -0.12250852111560884,
      -0.08925257072564526,
      -0.05454667420070938,
      -0.018963267725087034,
      0.01692170487317521,
      0.052546703529492464,
      0.08738021598631547
    ],
    [
      0.023176482170049893,
      -0.03570890175191642,
      -0.09418200904341184,
      -0.15157628703083262,
      -0.20725018507192428,
      -0.26059372954216387,
      -0.3110342245869351,
      -0.35804109426867037,
      -0.4011299045094254,
      -0.4398656199813014,
      -0.47386516216852154,
      -0.5027993408110512,
      -0.5263942325523437,
      -0.5444320786345661,
      -0.5567517686592786,
      -0.5632489704577311,
      -0.5638759575993604,
      -0.5586411765183236,
      -0.5476085850745737,
      -0.5308967839186444,
      -0.5086779515619733,
      -0.48117658378220024,
      -0.4486680281001905,
      -0.41147679473124227,
      -0.36997461682433586,
      -0.3245782251813146,
      -0.2757467962532365,
      -0.22397902736137543,
      -0.16980979016779146,
      -0.11380631285288464,
      -0.05656384373608227,
      0.0012992453078268112,
      0.05914694783345635,
      0.11633172271970935,
      0.1722012052717801,
      0.22610550704576665,
      0.2774050548024971,
      0.3254788882075748,
      0.369733302524856,
      0.40961068819041263,
      0.444598385885313,
      0.4742373460138868,
      0.49813035806509054,
      0.5159496009118969,
      0.5274432621465772,
      0.5324409849058607,
      0.5308579252543623,
      0.5226972418500632,
      0.5080508908130631,
      0.48709865969467536,
      0.46010544137064974,
      0.42741681702008855,
      0.38945308231728465,
      0.3467019080428359,
      0.29970987175636404,
      0.24907312833583653,
      0.19542750280309001,
      0.13943828903617575,
      0.08179002408021337,
      0.023176482170061623
    ],
    [
      -0.789022466028093,
      -0.8379049744536989,
      -0.8756491663336613,
      -0.9021736739338259,
      -0.9175631596996373,
      -0.9220448171118932,
      -0.9159653454978376,
      -0.8997694225179036,
      -0.8739803016544655,
      -0.8391828438659473,
      -0.7960090568564787,
      -0.7451260543871511,
      -0.6872262483205783,
      -0.6230195329305513,
      -0.5532272008650874,
      -0.47857733175762507,
      -0.39980140915028217,
      -0.31763194276930556,
      -0.23280089690760747,
      -0.14603874882480639,
      -0.058074021884029534,
      0.030366844445045216,
      0.11855941213860263,
      0.20578106492053108,
      0.2913108416094477,
      0.3744293922331532,
      0.45441917035614754,
      0.5305649829838274,
      0.6021550313468682,
      0.6684825914938328,
      0.7288485023040223,
      0.7825646492036251,
      0.8289586527364615,
      0.8673799894128568,
      0.8972077837364468,
      0.9178605089855686,
      0.9288078120242576,
      0.9295846235928894,
      0.9198076175407242,
      0.8991939266050231,
      0.8675817961394093,
      0.8249525533802688,
      0.7714528922426461,
      0.7074160445684582,
      0.6333799763408906,
      0.5501003898005988,
      0.45855613441199217,
      0.3599447468340869,
      0.2556663486552636,
      0.1472950667665751,
      0.036538442277009316,
      -0.07481321885722046,
      -0.18494419987391522,
      -0.2920730670217977,
      -0.3945099566369628,
      -0.49070705401910025,
      -0.579298852781637,
      -0.659130186603379,
      -0.7292715377962705,
      -0.7890224660280826
    ],
    [
      -1.0144850694026708,
      -1.0152561728481817,
      -1.005071605493875,
      -0.9840484842129326,
      -0.952414893124838,
      -0.9105077331355091,
      -0.8587698087338885,
      -0.797746150633676,
      -0.7280795654368537,
      -0.650505397584391,
      -0.5658454849265725,
      -0.4750012877595636,
      -0.37894617257301794,
      -0.27871683643650697,
      -0.1754038662601479,
      -0.07014143933950583,
      0.03590381223467981,
      0.14154473060427608,
      0.24558628808544877,
      0.3468388764619946,
      0.4441321065686717,
      0.5363289641949298,
      0.6223401373525307,
      0.7011383004689903,
      0.7717721148756452,
      0.8333796839348239,
      0.8852011871617097,
      0.9265904124439776,
      0.9570249103268043,
      0.9761145102186167,
      0.983607965578627,
      0.9793975332658191,
      0.9635213401085968,
      0.9361634455325357,
      0.8976515702806127,
      0.8484525249465402,
      0.7891654350524007,
      0.7205129185785709,
      0.6433304242983982,
      0.5585539825547731,
      0.4672066524688555,
      0.3703839699726476,
      0.269238709277949,
      0.16496526695787564,
      0.05878396388355346,
      -0.04807446249240251,
      -0.15438393259857117,
      -0.25893745730700746,
      -0.36056075675881843,
      -0.458124930987709,
      -0.5505580840853264,
      -0.6368558372858678,
      -0.7160906963500869,
      -0.7874202642910351,
      -0.8500943114165918,
      -0.9034607307688041,
      -0.9469704184404123,
      -0.9801811252579619,
      -1.0027603293718443,
      -1.0144871789100107
    ]
  ];

  arcDataZ = [
    [
      0.00011223491434057905,
      0.00007586724800554962,
      0.00003841737201247418,
      4.361820122137807e-7,
      -0.000037553866424924087,
      -0.0000750667206222879,
      -0.00011165790638920746,
      -0.00014692730128750155,
      -0.00018051996240959058,
      -0.0002121254720602791,
      -0.00024147622816780243,
      -0.00026834504617448526,
      -0.00029254237051604825,
      -0.000313913326545879,
      -0.0003323347836377191,
      -0.00034771254978180273,
      -0.0003599787777791325,
      -0.00036908963240561867,
      -0.0003750232453580741,
      -0.00037777796889464435,
      -0.0003773709283911645,
      -0.000373836867253718,
      -0.00036722727366327696,
      -0.0003576097765852396,
      -0.00034506779763184513,
      -0.00032970044512809117,
      -0.0003116226366134297,
      -0.00029096543557093535,
      -0.00026787658700735164,
      -0.0002425212341907209,
      -0.00021508279493538985,
      -0.00018576396980744263,
      -0.000154787845945295,
      -0.0001223990482423922,
      -0.00008886487378471985,
      -0.00005447632512875903,
      -0.000019548932841297365,
      0.00001557677223785019,
      0.000050535310567654537,
      0.0000849370390159234,
      0.00011836991387291981,
      0.0001504025034099397,
      0.0001805885328767146,
      0.00020847322861880635,
      0.00023360167076803538,
      0.0002555292521608618,
      0.000273834164867125,
      0.00028813159423106064,
      0.00029808900827024904,
      0.0003034416226293697,
      0.00030400685380845856,
      0.00029969641528605415,
      0.00029052472861498807,
      0.0002766125550547031,
      0.00025818519645612407,
      0.00023556520456417763,
      0.00020916016758395253,
      0.00017944668704971312,
      0.00014695201264972485,
      0.00011223491434058562
    ],
    [
      -0.007899821430635437,
      -0.008055856771281528,
      -0.008118643889316731,
      -0.008088342969737821,
      -0.007966183707164867,
      -0.007754411065964656,
      -0.007456222083890157,
      -0.0070756959753829635,
      -0.006617719570260645,
      -0.0060879098578009855,
      -0.005492535117066823,
      -0.004838435819811662,
      -0.004132946206858625,
      -0.003383817172806166,
      -0.002599140854450294,
      -0.0017872771096235668,
      -0.0009567818972126405,
      -0.00011633742646720535,
      0.0007253161663046977,
      0.001559447933988991,
      0.0023774023963257797,
      0.0031706653663343765,
      0.00393092970340579,
      0.004650161333510912,
      0.0053206658116305774,
      0.0059351556079676624,
      0.0064868181774837596,
      0.006969384721638566,
      0.007377199372255537,
      0.007705288321686443,
      0.007949428193686806,
      0.008106212700722518,
      0.008173116373276633,
      0.008148553885825818,
      0.008031933256431905,
      0.007823700979738954,
      0.007525376986873464,
      0.007139577232718232,
      0.00667002171418407,
      0.006121525843850037,
      0.005499973358818822,
      0.004812269344926675,
      0.004066272501438518,
      0.003270706448593607,
      0.0024350506633026413,
      0.00156941247676365,
      0.0006843824295617558,
      -0.00020912390501361548,
      -0.0011000338126674863,
      -0.0019772906892179566,
      -0.0028300279710052906,
      -0.0036477389842841305,
      -0.004420438126628404,
      -0.005138809207117637,
      -0.005794337392230524,
      -0.006379421980499046,
      -0.006887468099955031,
      -0.007312956323629749,
      -0.007651490068573273,
      -0.007899821430635397
    ],
    [
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0,
      0
    ],
    [
      0.00004946737211370695,
      0.00004954385586379288,
      0.0000490853493109892,
      0.0000480963178612545,
      0.0000465866727437646,
      0.00004457168699040732,
      0.00004207187355364273,
      0.000039112825289043194,
      0.00003572501616470678,
      0.00003194356276675536,
      0.00002780794497233494,
      0.000023361684582738125,
      0.000018651980771914463,
      0.00001372930143056594,
      0.0000086469298912549,
      0.000003460467118831415,
      -0.000001772709749150695,
      -0.000006994030626410647,
      -0.000012144354389406588,
      -0.00001716462504089372,
      -0.000021996556200493558,
      -0.00002658333642752361,
      -0.00003087034629709703,
      -0.00003480587663030911,
      -0.000038341835912343235,
      -0.00004143443381330135,
      -0.00004404482695034992,
      -0.00004613971268376674,
      -0.000047691856895823654,
      -0.000048680542409215234,
      -0.000049091925979873434,
      -0.00004891929363159832,
      -0.00004816320643358071,
      -0.0000468315315671741,
      -0.00004493935656405843,
      -0.000042508787779536876,
      -0.000039568637334379673,
      -0.0000361540057576488,
      -0.00003230577024428242,
      -0.000028069990680317764,
      -0.000023497247292868073,
      -0.000018641924895815287,
      -0.000013561459209525627,
      -0.000008315560655459151,
      -0.000002965430418378617,
      0.0000024270174892493687,
      0.000007799915837273617,
      0.000013092174796734302,
      0.000018244174700681913,
      0.000023198413919757594,
      0.000027900106619090212,
      0.000032297726838668,
      0.00003634349684225712,
      0.00003999381897273998,
      0.00004320965131299502,
      0.000045956828274329675,
      0.000048206327823426065,
      0.00004993448742792456,
      0.00005112317097055891,
      0.000051759888877364465
    ]
  ];

  constructor() { }

  getPosition(index: number): number[] {
    return this.testData[index];
  }

  getOrbitPath(index: number): THREE.CatmullRomCurve3 {
    const data = [];
    for (let i = 0; i < this.arcDataX[index].length; i++) {
      data.push(new THREE.Vector3(this.arcDataX[index][i], this.arcDataY[index][i], this.arcDataZ[index][i]));
    }
    return new THREE.CatmullRomCurve3(data);
  }
}