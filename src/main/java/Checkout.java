import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

public class Checkout {
    public String tool_code;
    public int num_rental_days;
    public LocalDate check_out_date;
    public LocalDate due_date;
    public int num_charge_days; //the number of days the item is being charged
    public double pre_discount_charge_amount;
    public int discount_percent;
    public double discount_amount;
    public double final_charge_amount;  //final amount charged to customer
    Tools rental_tool;


    public Checkout(String toolCode, int numRentalDays, int discountPercent, LocalDate checkOutDate) throws Exception {
        tool_code = toolCode;
        rental_tool = new Tools(tool_code);
        num_rental_days = numRentalDays;
        discount_percent = discountPercent;
        check_out_date = checkOutDate;
        due_date = calculateDueDate(check_out_date, num_rental_days);
        num_charge_days = calculateNumChargeDays(check_out_date, due_date, rental_tool);
        pre_discount_charge_amount = calculatePreDiscountChargeAmount(rental_tool,num_charge_days);
        discount_amount = calculateDiscountAmount(pre_discount_charge_amount,discount_percent);
        final_charge_amount = calculateFinalChargeAmount(pre_discount_charge_amount, discount_amount);
        printRentalAgreement();
    }


    public LocalDate calculateDueDate(LocalDate checkOutDate, int numRentalDays){
        return checkOutDate.plusDays(numRentalDays);
    }


    public int calculateNumChargeDays(LocalDate checkOutDate, LocalDate dueDate, Tools tool){
        int count = 0;
        LocalDate date = checkOutDate;
        ArrayList<LocalDate> holidays = getHolidays(checkOutDate.getYear());
        while (date.isBefore(dueDate)){
            if(holidays.contains(date)){    //weekend check
                if(tool.holiday_charge)
                    count++;
            } else if (date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {    //weekend check
                if(tool.weekend_charge)
                    count++;
            } else if (date.getDayOfWeek().equals(DayOfWeek.MONDAY) || date.getDayOfWeek().equals(DayOfWeek.TUESDAY) || date.getDayOfWeek().equals(DayOfWeek.WEDNESDAY) || date.getDayOfWeek().equals(DayOfWeek.THURSDAY) || date.getDayOfWeek().equals(DayOfWeek.FRIDAY)) {  //weekday check
                if(tool.weekday_charge)
                    count++;
            }
            date = date.plusDays(1);
        }
        return count;
    }


    public ArrayList<LocalDate> getHolidays(int year){
        ArrayList<LocalDate> holidays = new ArrayList<>();

        //Independence Day
        LocalDate ind_day = LocalDate.of( year, 7, 4);
        if(ind_day.getDayOfWeek().equals(DayOfWeek.SATURDAY)){
            ind_day = ind_day.minusDays(1);
        } else if (ind_day.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            ind_day = ind_day.plusDays(1);
        }
        holidays.add(ind_day);


        //Labor Day
        LocalDate labor_day = LocalDate.of(year, 9, 1);
       // for(int i = 0; i < 7; i++){
        while(labor_day.getDayOfMonth() <= 7){
            if(labor_day.getDayOfWeek().equals(DayOfWeek.MONDAY)){
                break;
            }
            labor_day = labor_day.plusDays(1);
        }
        holidays.add(labor_day);

        return holidays;
    }


    public double calculatePreDiscountChargeAmount(Tools tools, int numChargeDays){
        double pre_discount_charge_amount = tools.daily_rental_charge * numChargeDays;
        pre_discount_charge_amount = Math.floor((pre_discount_charge_amount*100)+.5)/100; //round up and leave only 2 decimal points
        return pre_discount_charge_amount;
    }


    public double calculateDiscountAmount(double preDiscountChargeAmount, int discountPercent){
        double discount_amount = preDiscountChargeAmount * (discountPercent/100.0);
        discount_amount = Math.floor((discount_amount*100)+.5)/100; //round up and leave only 2 decimal points
        return discount_amount;
    }


    public double calculateFinalChargeAmount(double preDiscountChargeAmount, double discountAmount){
        double final_charge_amount = preDiscountChargeAmount - discountAmount;
        final_charge_amount = Math.floor((final_charge_amount*100)+.5)/100; //round up and leave only 2 decimal points
        return final_charge_amount;
    }


    public void printRentalAgreement(){
        String rental_agreement =
                "Tool Code: " + tool_code +
                "\nTool Type: " + rental_tool.tool_type +
                "\nTool Brand: " + rental_tool.tool_brand +
                "\nRental Days: " + num_rental_days + " Days" +
                "\nCheck Out Date: " + check_out_date +
                "\nDue Date: " + due_date +
                "\nDaily Rental Charge: $" + rental_tool.daily_rental_charge + "/day" +
                "\nCharge Days: " + num_charge_days + " Days" +
                "\nPre-Discount Amount: $" + pre_discount_charge_amount +
                "\nDiscount Percent: " + discount_percent + "%" +
                "\nDiscount Amount: $" + discount_amount +
                "\nFinal Charge: $" + final_charge_amount;

        System.out.println(rental_agreement);
    }
}