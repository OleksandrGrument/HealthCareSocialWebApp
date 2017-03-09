package com.ComeOnBaby.controller;

import com.ComeOnBaby.comparator.NoteByDateComparator;
import com.ComeOnBaby.model.AppUser;
import com.ComeOnBaby.model.BasicQuestions;
import com.ComeOnBaby.model.Note;
import com.ComeOnBaby.service.AppUserService;
import com.ComeOnBaby.service.BasicQuestionsService;
import com.ComeOnBaby.service.NoteService;
import com.ComeOnBaby.util.BasicQuestionsForm;
import com.ComeOnBaby.util.DataNoteByMonthWeek;
import com.ComeOnBaby.XlsxView.MonthlyWeeklyReportShowXlsx;
import com.ComeOnBaby.XlsxView.AllAppUsersInfoXlsx;
import com.ComeOnBaby.util.WeekReportInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.*;


@Controller
@RequestMapping("/users")
@SessionAttributes("roles")
public class UserManagementController {


    @Autowired
    AppUserService appUserService;

    @Autowired
    NoteService noteService;

    @Autowired
    BasicQuestionsService basicQuestionsService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView indexCabinet() {

        ModelAndView cabinetUsersList = new ModelAndView("managerCabinet");

        List<AppUser> users = appUserService.getAllUsers();

        cabinetUsersList.addObject("users", users);

        return cabinetUsersList;
    }


    @RequestMapping(value = "/user-profile/{userId}", method = RequestMethod.GET)
    public ModelAndView userProfile(@PathVariable Long userId) {

        ModelAndView userProfile = new ModelAndView("userProfile");

        AppUser user = appUserService.findById(userId);

        userProfile.addObject("user", user);

        return userProfile;
    }

    @RequestMapping(value = "/basic-questions/{userId}", method = RequestMethod.GET)
    public ModelAndView baseQuestions(@PathVariable Long userId) {

        ModelAndView basicQuestionsView = new ModelAndView("basicQuestions");
        AppUser user = appUserService.findById(userId);

        BasicQuestions basicQuestions = basicQuestionsService.readBasicQuestionsByUser(user);
        BasicQuestionsForm basicQuestionsForm = new BasicQuestionsForm(basicQuestions);

        List<String> listQuestion1 = basicQuestionsForm.question1();
        List<String> listQuestion2 = basicQuestionsForm.question2();
        List<String> listQuestion3 = basicQuestionsForm.question3();
        List<String> listQuestion4 = basicQuestionsForm.question4();
        List<String> listQuestion5 = basicQuestionsForm.question5();
        List<String> listQuestion6 = basicQuestionsForm.question6();
        List<String> listQuestion7 = basicQuestionsForm.question7();

        basicQuestionsView.addObject("user", user);
        basicQuestionsView.addObject("listQuestion1", listQuestion1);
        basicQuestionsView.addObject("listQuestion2", listQuestion2);
        basicQuestionsView.addObject("listQuestion3", listQuestion3);
        basicQuestionsView.addObject("listQuestion4", listQuestion4);
        basicQuestionsView.addObject("listQuestion5", listQuestion5);
        basicQuestionsView.addObject("listQuestion6", listQuestion6);
        basicQuestionsView.addObject("listQuestion7", listQuestion7);

        return basicQuestionsView;
    }

    @RequestMapping(value = "/monthly-report/{userId}", method = RequestMethod.GET)
    public ModelAndView monthlyReport(@PathVariable Long userId) {

        ModelAndView monthlyReport = new ModelAndView("monthlyReport");

        AppUser user = appUserService.findById(userId);
        List<Note> notices = noteService.findUserNotes(user);

        monthlyReport.addObject("user", user);
        monthlyReport.addObject("notices", notices);

        return monthlyReport;
    }

    @RequestMapping(value = "/weekly-report/{userId}", method = RequestMethod.GET)
    public ModelAndView weeklyReport(@PathVariable Long userId) {

        ModelAndView weeklyReport = new ModelAndView("weeklyReport");
        AppUser user = appUserService.findById(userId);
        List<Note> notes = noteService.findUserNotes(user);
        Collections.sort(notes, new NoteByDateComparator());

        DataNoteByMonthWeek dataNoteByWeek = new DataNoteByMonthWeek(notes);

        weeklyReport.addObject("user", user);
        weeklyReport.addObject("weekReportInformation", dataNoteByWeek.weekReportInformation());

        return weeklyReport;
    }


    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ModelAndView download() {

        AllAppUsersInfoXlsx xlsxView = new AllAppUsersInfoXlsx();
        xlsxView.setAppUserList(appUserService.getAllUsers());
        return new ModelAndView(xlsxView);
    }

    @RequestMapping(value = "/monthlyReportShow/{userId}/{month}/{year}", method = RequestMethod.GET)
    public ModelAndView monthlyReportShow(@PathVariable Long userId, @PathVariable int month, @PathVariable int year) {

        ModelAndView monthlyReport = new ModelAndView("monthlyReportShow");

        AppUser user = appUserService.findById(userId);
        List<Note> notices = noteService.findUserNotes(user);
        DataNoteByMonthWeek dataNoteByMonth = new DataNoteByMonthWeek(notices, month, year);

        String daysInMonthsString = dataNoteByMonth.daysInMonthsString();
        String valueInMonthsString = dataNoteByMonth.valueInMonthsString();

        monthlyReport.addObject("user", user);
        monthlyReport.addObject("dataNoteByMonth", dataNoteByMonth);
        monthlyReport.addObject("month", month);
        monthlyReport.addObject("year", year);
        monthlyReport.addObject("daysInMonthsString", daysInMonthsString);
        monthlyReport.addObject("valueInMonthsString", valueInMonthsString);

        return monthlyReport;
    }

    @RequestMapping(value = "/downloadMonthlyReport/{userId}/{month}/{year}", method = RequestMethod.GET)
    public ModelAndView downloadMonthlyReport(@PathVariable Long userId, @PathVariable int month, @PathVariable int year) {

        AppUser user = appUserService.findById(userId);
        List<Note> notices = noteService.findUserNotes(user);
        DataNoteByMonthWeek dataNoteByMonth = new DataNoteByMonthWeek(notices, month, year);

        MonthlyWeeklyReportShowXlsx monthlyReportShowXlsx = new MonthlyWeeklyReportShowXlsx();
        monthlyReportShowXlsx.setDataNoteByMonthWeek(dataNoteByMonth);
        return new ModelAndView(monthlyReportShowXlsx);
    }

    @RequestMapping(value = "/weeklyReportShow/{userId}/{countWeekOfYear}", method = RequestMethod.GET)
    public ModelAndView weeklyReportShow(@PathVariable Long userId, @PathVariable int countWeekOfYear) {
        ModelAndView weeklyReportShow = new ModelAndView("weeklyReportShow");


        System.out.println("countWeekOfYear in controller "+ countWeekOfYear);
        AppUser user = appUserService.findById(userId);
        List<Note> notes = noteService.findUserNotes(user);

        DataNoteByMonthWeek dataNoteByWeek = new DataNoteByMonthWeek(notes, countWeekOfYear);
        WeekReportInformation weekReportInformation = new WeekReportInformation(dataNoteByWeek.getDataNoteByMonthWeek().get(0).getDate());


        weeklyReportShow.addObject("user", user);
        weeklyReportShow.addObject("weekReportInformation", weekReportInformation);
        weeklyReportShow.addObject("dataNoteByWeek", dataNoteByWeek);

        weeklyReportShow.addObject("daysInWeekString", dataNoteByWeek.daysInWeekString());
        weeklyReportShow.addObject("valueInWeekString", dataNoteByWeek.valueInWeekString());

        return weeklyReportShow;
    }

    @RequestMapping(value = "/downloadWeeklyReport/{userId}/{countWeekOfYear}", method = RequestMethod.GET)
    public ModelAndView downloadWeeklyReport(@PathVariable Long userId, @PathVariable int countWeekOfYear) {

        AppUser user = appUserService.findById(userId);
        List<Note> notes = noteService.findUserNotes(user);
        Collections.sort(notes, new NoteByDateComparator());

        DataNoteByMonthWeek dataNoteByWeek = new DataNoteByMonthWeek(notes, countWeekOfYear);

        MonthlyWeeklyReportShowXlsx weekyReportShowXlsx = new MonthlyWeeklyReportShowXlsx();
        weekyReportShowXlsx.setDataNoteByMonthWeek(dataNoteByWeek);
        return new ModelAndView(weekyReportShowXlsx);
    }
}
