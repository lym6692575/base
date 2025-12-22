/**
 * unix time
 * @param timestamp
 * @returns 12/30/2018 18:00 PM
 */
export function unix2CurrentTime(timestamp) {
  if(timestamp!=null)
  return new Date(timestamp).toLocaleString()
}
export function unix2CurrentTime2(timestamp) {
    if(timestamp!=null){
        let date = new Date(timestamp);
        let Y = date.getFullYear() + '-';
        let M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '-';
        let D = (date.getDate() < 10 ? ('0'+date.getDate()) : date.getDate()) + ' ';
        let h = (date.getHours() < 10 ? ('0'+date.getHours()) : date.getHours()) + ':';
        let m =  (date.getMinutes() < 10 ? ('0'+date.getMinutes()) : date.getMinutes()) + ':';
        let s =(date.getSeconds() < 10 ? ('0'+date.getSeconds()) : date.getSeconds()) ;
        return Y+M+D+h+m+s;
    }
}
export function unix2CurrentDate(timestamp) {
    if(timestamp!=null){
        let str=new Date(timestamp);
        let year=str.getFullYear();
        let month=str.getMonth()+1;
        if(month<10){
            month='0'+(str.getMonth()+1);
        }
        let day=str.getDate();
        if(day<10){
            day='0'+(str.getDate());
        }
        return year+'-'+month+'-'+day;
    }
}
