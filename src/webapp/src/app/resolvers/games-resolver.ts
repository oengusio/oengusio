import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Game } from '../../model/game';
import { GameService } from '../../services/game.service';

@Injectable()
export class GamesResolver implements Resolve<Game[]> {

  constructor(private gameService: GameService) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Game[]> | Promise<Game[]> | Game[] {
    return this.gameService.getAllForMarathon(route.parent.paramMap.get('id'));
  }
}
