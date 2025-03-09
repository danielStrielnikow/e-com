import { CURRENCY } from "../constants";
export const formatPrice = (amount) => {
    return new Intl.NumberFormat("en-US", {
       style: "currency",
       currency: CURRENCY,
    }).format(amount);
   }
   
   
   export const formatPriceCalculation = (quantity, price) => {
      return (Number(quantity) * Number(price)).toFixed(2);
     }