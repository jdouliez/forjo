import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { RemediationService } from './remediation.service';
import { animate, state, transition, trigger, style, keyframes } from '@angular/animations';
import remediationsMessages  from '../../assets/json/remediations.json';
import helpMessages from '../../assets/json/helpMessages.json';
import { BaseChartDirective } from 'ng2-charts';
import { ViewChild } from '@angular/core'
import introJs from 'intro.js/intro.js';
import {MatProgressBar} from '@angular/material/progress-bar';

@Component({
  selector: 'app-remediation',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './remediation.component.html',
  styleUrls: ['remediation.component.scss'],
  animations:[
    trigger('show', [
      transition(':enter', [
        style({ opacity: 0 }),
        animate(200, style({ opacity: 1 }))
      ]),
      transition(':leave', [
        style({ opacity: 1 }),
        animate(300, style({ opacity: 0 }))
      ])
    ])
  ]
})
export class RemediationComponent implements OnInit {
  @ViewChild(BaseChartDirective)
  public chart: BaseChartDirective;
  public remediations : Array<any>;
  public responses : Array<any>;
  public panel: number = 1;
  public showResult: boolean;
  public showHelp: boolean;

  public isSelected: boolean = false;
  public selected: number = -1;
  public score: number = 0;
  public scoreMax: number = 240;
  public pieChartData: number[] = [3, 5];
  public pieChartLabels: Array<any> = ['Passed', 'Failed'];
  public pieChartType: string = 'pie';
  public pieChartLegend = true;

  public pieChartColors = [{
    backgroundColor: ['#39aa56', '#DB4545'],
  }];


  public isLoading = false;

  public radarChartLabels = ['A01', 'A02', 'A03', 'A04', 'A05', 'A06', 'A07', 'A08', 'A09', 'A10'];
  public radarChartData = [
    { data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], label: 'Success' },
    { data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], label: 'Failed' }
  ];

  public radarChartType = 'radar';
  public chartColors: any[] = [{
    backgroundColor: '#39aa566e',
    borderColor: '#39aa56',
    pointBackgroundColor: '#39aa56',
    pointBorderColor: '#39aa56',
    pointHoverBackgroundColor: '#39aa56',
    pointHoverBorderColor: '#39aa56'
  },
    {
      backgroundColor: '#db45456e',
      borderColor: '#DB4545',
      pointBackgroundColor: '#DB4545',
      pointBorderColor: '#DB4545',
      pointHoverBackgroundColor: '#DB4545',
      pointHoverBorderColor: '#DB4545'
    }];

  public lineChartOptions: any = {
    scale: {
      ticks: {
        beginAtZero: true,
        max: 100,
        min: 0,
        stepSize: 10
      }
    }
  };

  public introJS = introJs();

  constructor(private remediationService : RemediationService) {}

  updateScore() {
    this.scoreMax = 0;
    this.score = 0;
    for (const remediation of this.remediations) {
      this.scoreMax += remediation.total * 10;
      this.score += remediation.passed * 10;
    }
    if(this.scoreMax === 0) {
      this.scoreMax = 240;
    }
  }

  ngOnInit() {
    this.showResult = false;
    this.showHelp = false;
    this.remediations = [];
    let remediationsLocal = localStorage.getItem('remediations');
    if (remediationsLocal) {
      this.remediations = JSON.parse(remediationsLocal);
      this.updateScore();
      for (let remediation of this.remediations) {
        this.radarChartData[0].data[remediation.id] = remediation.passed * 100 / remediation.total;
        this.radarChartData[1].data[remediation.id] = (remediation.total-remediation.passed) * 100 / remediation.total;
      }
      return true;
    }

    for (let i = 0; i <= 9; i++) {
      let topOwasp = "A" + (i+1).toString().padStart(2, '0');
      this.remediations[i] = {};
      this.remediations[i].id = i;
      this.remediations[i].slug = topOwasp;
      this.remediations[i].state = "canceled";
      this.remediations[i].total = 0;
      this.remediations[i].passed = 0;
      this.remediations[i].title = remediationsMessages[topOwasp].title;
      this.remediations[i].messages = remediationsMessages[topOwasp];

      this.remediationService.checkFunctional(topOwasp)
        .then(res => {
          let disable = (res.functional == "false");
          this.remediations[i]["disable"] = disable;
        })
        .catch(error => {
          console.log(error);
        });
    }
  }

  async runRemediation(id: number, topOwasp: string) {
    await this.remediationService.checkFunctional(topOwasp)
      .then(res => {
        if (res.functional == "false") {
          this.remediations[id]["disable"] = true;
          localStorage.setItem('remediations', JSON.stringify(this.remediations));
          return null
        }
        else {
          this.remediations[id]["disable"] = false;
          return this.remediationService.checkRemediation(topOwasp)
            .then(res => {
              if (res != "NOT FUNCTIONAL") {
                this.showResult = true;
                this.responses = JSON.parse(res);

                let total = this.responses.length;
                let passed = 0;
                let i = 0;
                for (let response of this.responses) {
                  if (response.success) {
                    passed++;
                  }
                  response.id = i++;
                  response.state = (response.success) ? "passed" : "failed";
                }
                this.remediations[id].data = this.responses;
                this.remediations[id].passed = passed;
                this.remediations[id].total = total;
                this.remediations[id].failed = total-passed;
                this.radarChartData[0].data[id] = passed * 100 / total;
                this.radarChartData[1].data[id] = (total-passed) * 100 / total;
                this.remediations[id].state = (total == passed) ? "passed" : "failed";
                localStorage.setItem('remediations', JSON.stringify(this.remediations));
              }
            })
            .catch(error => {
              console.log(error);
            });
        }
      })
      .catch(error => {
        console.log(error);
      });
  }

  showHelpAction() {
    this.showHelp = true;
  }

  showDetail(id: number) {
    this.isSelected = true;
    this.selected = id;
    this.updateTopChart(id);
    this.updateScore();
  }

  updateTopChart(id: number) {
    if (this.remediations[id].disable == false) {
      this.pieChartData[0] = this.remediations[id].passed;
      this.pieChartData[1] = this.remediations[id].failed;
    } else {
      this.pieChartData[0] = 0;
      this.pieChartData[1] = 0;
    }
    this.chart.chart.update();
  }

  switchPanel (id: number) {
    this.panel = id;
  }

  showAll() {
    this.isSelected = false;
    this.selected = -1;
  }

  async runAllTests() {
    this.isLoading = true;
    for(let remediation of this.remediations) {
      await this.runRemediation(remediation.id, remediation.slug)
    }
    this.chart.chart.update();
    this.isLoading = false;
    this.updateScore();
  }

  async runTest(id: number) {
    this.isLoading = true;
    await this.runRemediation(id, this.remediations[id].slug)
    this.updateTopChart(id);
    this.updateScore();
    this.isLoading = false;
  }

  hide() {
    this.showResult = false;
    this.showHelp = false;
  }

  toggleClass(event: any, classname: string) {
    const hasClass = event.target.classList.contains(classname);

    if(hasClass) {
      event.target.classList.remove(classname);
    } else {
      event.target.classList.add(classname);
    }
  }

  help() {
    if (this.selected === -1) {
      this.introJS.setOptions(helpMessages.all);
      this.introJS.onbeforechange(() => {})
      this.introJS.onafterchange(() => {})
    } else {
      this.introJS.setOptions(helpMessages.top);
      this.introJS.onbeforechange(function(element) {
        var hasRun = (document.querySelectorAll("#top_operational_test .job_list li").length != 0)
        if (this._currentStep == 2) {
          element.click();
        }
        if (this._currentStep == 4) {
          element.click();
        }
        if (this._currentStep == 7 && hasRun) {
          document.querySelector("#top_operational_test .job_list li").classList.add("show-content")
        }
      })
      this.introJS.onafterchange(function(element) {
        var hasRun = (document.querySelectorAll("#top_operational_test .job_list li").length != 0)
        if (this._currentStep == 8) {
          if (!hasRun) {
            if (this._direction == "forward") {
              this.goToStepNumber(10)
              return false
            }
            if (this._direction == "backward") {
              this.goToStepNumber(8)
              return false
            }
          } else {

          }
        }
      })
    }
    this.introJS.start();
  }
}
