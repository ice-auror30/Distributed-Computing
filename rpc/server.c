#include <rpc/rpc.h>
#include <time.h>
#include <sys/types.h>
#include <linux/kernel.h>
#include <sys/sysinfo.h>
#include <stdio.h>
#include <stdlib.h>
#include "date.h"

#define MAX_LEN 100


char **date_1(long *option)
{
  struct tm *timeptr;
  time_t clock;
  static char *ptr;
  static char err[] = "Invalid Response \0";
  static char s[MAX_LEN];

  clock = time(0);
  timeptr = localtime(&clock);
  switch(*option)
  {
	case 1:strftime(s,MAX_LEN,"%A, %B %d, %Y",timeptr);
	  ptr=s;
	  break;

	case 2:strftime(s,MAX_LEN,"%T",timeptr);
	  ptr=s;
	  break;
	
	case 3:strftime(s,MAX_LEN,"%A, %B %d, %Y - %T",timeptr);
	  ptr=s;
	  break;

	default: ptr=err;
	  break;
  }

  return(&ptr);
}

double *memory_1(void)
{
static double num1;
long long totalram;
long long freeram;
struct sysinfo info;
sysinfo(&info);
totalram = info.totalram;
freeram = info.freeram;
num1 = (double)(totalram-freeram)/(double) (totalram);
return(&num1);
}

double *process_1(void)
{
  static double load[3];
  static double negative;
  if (getloadavg(load, 3) != -1)
     {
        return (&load[0]);
     }
}

double *cpu_1(void)
{
//please implement
//Function: FILE *fopen(const char *pathname, const char *mode);
//http://man7.org/linux/man-pages/man3/fopen.3.html
//Function: int fscanf(FILE *stream, const char *format, ...);
//http://man7.org/linux/man-pages/man3/scanf.3.html
//Function: int fclose(FILE *stream);
//http://man7.org/linux/man-pages/man3/fclose.3.html
//Function: unsigned int sleep(unsigned int seconds);
//http://man7.org/linux/man-pages/man3/sleep.3.html
    long double cpu_reading[4], cpu_reading2[4];
    static double cpu_avg;
    FILE *file;


        file = fopen("/proc/stat","r");
        fscanf(file,"%*s %Lf %Lf %Lf %Lf",&cpu_reading[0],&cpu_reading[1],&cpu_reading[2],&cpu_reading[3]);
        fclose(file);
        sleep(1);

        file = fopen("/proc/stat","r");
        fscanf(file,"%*s %Lf %Lf %Lf %Lf",&cpu_reading2[0],&cpu_reading2[1],&cpu_reading2[2],&cpu_reading2[3]);
        fclose(file);

        cpu_avg = ((cpu_reading2[0]+cpu_reading2[1]+cpu_reading2[2]) - (cpu_reading[0]+cpu_reading[1]+cpu_reading[2])) / ((cpu_reading2[0]+cpu_reading2[1]+cpu_reading2[2]+cpu_reading2[3]) - (cpu_reading[0]+cpu_reading[1]+cpu_reading[2]+cpu_reading[3]));

    return(&cpu_avg);

}

