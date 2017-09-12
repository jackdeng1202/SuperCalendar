### 一个功能强大的购票时间选择的日历神器

 ![supercalendar](https://github.com/jackdengchuangliang/SuperCalendar/super.gif)

##### 1.设置前月是否显示其他月的日期
    mAdapter.setShowOthersMonthDays(true);
    
##### 2.设置日期选择模式
	gridView.setChoiceMode(CHOOSE_MODE);//1 单选模式 2 多选模式
    
##### 3.设置特殊日期的说明
	specialDay = new String[]{"20170912,111", "20170915,222"};
    mAdapter.setSpecialDay(specialDay);
    
待续......


****说明****

该库改编自https://github.com/LongYanL/Calendar-master ,感谢你的开源库!

